package com.jbs.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Lists;
import com.jbs.entity.Employee;
import com.jbs.entity.Section;
import com.jbs.entity.Shift;
import com.jbs.entity.Site;
import com.jbs.repository.EmployeeRepository;
import com.jbs.repository.SectionRepository;
import com.jbs.repository.ShiftRepository;
import com.jbs.repository.SiteRepository;
import com.jbs.repository.datatable.EmployeeTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by rizkykojek on 2/18/17.
 */
@Controller
public class EmployeeController {

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private EmployeeTableRepository employeeTableRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getIndex() {
        return "index";
    }

    @RequestMapping(value = "/summary/{employeeId}", method = RequestMethod.GET)
    public String getSummary(@PathVariable final Long employeeId, final Model model) {
        Employee employee = employeeRepository.findOne(employeeId);
        model.addAttribute(employee);
        return "summary";
    }

    @RequestMapping(value = "/duties", method = RequestMethod.GET)
    public String getDuties() {
        return "duties";
    }

    @RequestMapping(value = "/wcmc", method = RequestMethod.GET)
    public String getWcmc() {
        return "wcmc";
    }

    @RequestMapping(value = "/performance_report", method = RequestMethod.GET)
    public String getPerformanceReport() {
        return "performance_report";
    }

    @RequestMapping(value = "/event_report", method = RequestMethod.GET)
    public String getEventReport() {
        return "event_report";
    }

    @ModelAttribute("listShift")
    public List<Shift> getListShift() {
        return Lists.newArrayList(shiftRepository.findAll());
    }

    @ModelAttribute("listSection")
    public List<Section> getListSection() {
        return Lists.newArrayList(sectionRepository.findAll());
    }

    @ModelAttribute("listSite")
    public List<Site> getListSite() {
        return Lists.newArrayList(siteRepository.findAll());
    }


    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/hrClearanceTable", method = RequestMethod.GET)
    public @ResponseBody DataTablesOutput getHrClearanceTable(@Valid DataTablesInput request) {
        AtomicInteger atomicInteger = new AtomicInteger(request.getStart() + 1);
        DataTablesOutput<Employee> results = employeeTableRepository.findAll(request);
        results.getData().stream().forEach(s -> s.setCounterNumber(atomicInteger.getAndIncrement()));
        return results;
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/absentTable", method = RequestMethod.GET)
    public @ResponseBody DataTablesOutput getAbsentTable(@Valid DataTablesInput request) {
        AtomicInteger atomicInteger = new AtomicInteger(request.getStart() + 1);
        DataTablesOutput<Employee> results = employeeTableRepository.findAll(request);
        results.getData().stream().forEach(s -> s.setCounterNumber(atomicInteger.getAndIncrement()));
        return results;
    }


}
