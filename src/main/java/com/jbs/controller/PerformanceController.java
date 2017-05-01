package com.jbs.controller;

import com.jbs.dto.PerformanceDto;
import com.jbs.entity.Employee;
import com.jbs.entity.Performance;
import com.jbs.repository.EmployeeRepository;
import com.jbs.repository.PerformanceRepository;
import com.jbs.service.PerformanceService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.jsolve.templ4docx.core.Docx;
import pl.jsolve.templ4docx.core.VariablePattern;
import pl.jsolve.templ4docx.variable.TextVariable;
import pl.jsolve.templ4docx.variable.Variables;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

/**
 * Created by rizkykojek on 3/5/17.
 */
@Controller
public class PerformanceController {

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PerformanceService performanceService;

    @RequestMapping(value = {"/performance/{performanceId}", "/employee/{employeeId}/performance"}, method = RequestMethod.GET)
    public String getPerformance(@PathVariable Optional<Long> employeeId, @PathVariable Optional<Long> performanceId, final Model model) {
        Employee employee = null;
        PerformanceDto performanceDto = null;

        if (performanceId.isPresent()) {
            Performance performance = performanceRepository.findOne(performanceId.get());
            employee = performance.getEmployee();
            performanceDto = convertToDto(performance);

        } else if (employeeId.isPresent()) {
            employee = employeeRepository.findOne(employeeId.get());
            performanceDto = new PerformanceDto();
        }

        model.addAttribute(performanceDto);
        model.addAttribute(employee);

        return "performance";
    }

    @RequestMapping(value = "/employee/{employeeId}/performance/create", method = RequestMethod.POST)
    public String create(@PathVariable Long employeeId, @Valid PerformanceDto performanceDto, BindingResult bindingResult, Model model) throws Exception {
        Employee employee = employeeRepository.findOne(employeeId);
        model.addAttribute(employee);

        if (!bindingResult.hasErrors()){
            Performance performance = convertToEntity(performanceDto, Optional.empty());
            performance = performanceService.save(performance, performanceDto.getFiles());
            model.addAttribute(convertToDto(performance));
        }
        return "performance";
    }

    @RequestMapping(value = "/employee/{employeeId}/performance/update/{performanceId}", method = RequestMethod.POST)
    public String update(@PathVariable Long employeeId, @PathVariable Optional<Long> performanceId, @Valid PerformanceDto performanceDto, BindingResult bindingResult, Model model) throws Exception {
        Employee employee = employeeRepository.findOne(employeeId);
        model.addAttribute(employee);

        if (!bindingResult.hasErrors()) {
            Performance performance = convertToEntity(performanceDto, performanceId);
            performance = performanceService.save(performance, performanceDto.getFiles());
            model.addAttribute(convertToDto(performance));
        }

        return "performance";
    }

    @RequestMapping(value = "/performance/generate_letter", method = RequestMethod.GET)
    public void generateLetter(HttpServletResponse response) {
        try {
            DefaultResourceLoader loader = new DefaultResourceLoader();

            Docx docx = new Docx(loader.getResource("classpath:com/jbs/doc/letter.docx").getInputStream());
            docx.setVariablePattern(new VariablePattern("${", "}"));

            // fill template
            Variables variables = preparingVariablesOnLetter();
            docx.fillTemplate(variables);

            // save filled .docx file
            File tempFile = File.createTempFile("tmp",".docx", new File (loader.getClassLoader().getResource("com/jbs/doc/").getPath()));
            docx.save(new FileOutputStream(tempFile, false));

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=letter.docx");
            response.setContentLength((int)tempFile.length());
            InputStream inputStream = new BufferedInputStream(new FileInputStream(tempFile));

            FileCopyUtils.copy(inputStream, response.getOutputStream());
        } catch (IOException ex) {
            throw new RuntimeException("IOError writing file to output stream");
        }
    }

    private Performance convertToEntity(PerformanceDto performanceDto, Optional<Long> performanceId) {
        Performance performance = null;
        if (performanceId.isPresent()) {
            performance = performanceRepository.findOne(performanceId.get());
            modelMapper.map(performanceDto, performance);
            performance.setId(performanceId.get());
        } else {
            performance = modelMapper.map(performanceDto, Performance.class);
        }

        return  performance;
    }

    private PerformanceDto convertToDto(Performance performance) {
        PerformanceDto performanceDto = modelMapper.map(performance, PerformanceDto.class);
        return performanceDto;
    }

    private Variables preparingVariablesOnLetter() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        Variables variables = new Variables();
        variables.addTextVariable(new TextVariable("${employee_id}", "1111222"));
        variables.addTextVariable(new TextVariable("${full_name}", "Derrick Stewart"));
        variables.addTextVariable(new TextVariable("${salutation}", "Mr."));
        variables.addTextVariable(new TextVariable("${title}", "Staff"));
        variables.addTextVariable(new TextVariable("${address1}", "6/10 Smith Street."));
        variables.addTextVariable(new TextVariable("${address2}", "Neutral Bay"));
        variables.addTextVariable(new TextVariable("${state}", "NSW"));
        variables.addTextVariable(new TextVariable("${code}", "12345"));
        variables.addTextVariable(new TextVariable("${date}", formatter.format(new Date())));
        variables.addTextVariable(new TextVariable("${company_name}", "JBS Australia Pty Limited"));
        variables.addTextVariable(new TextVariable("${officer_name}", "John Mckey"));
        variables.addTextVariable(new TextVariable("${officer_title}", "General Manager"));

        return variables;
    }
}
