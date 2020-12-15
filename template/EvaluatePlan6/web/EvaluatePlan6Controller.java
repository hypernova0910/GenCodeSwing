package com.tav.web.controller;

import com.google.common.base.Strings;
import com.tav.web.common.DateUtil;import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tav.common.web.form.JsonDataGrid;
import com.tav.web.bo.ServiceResult;
import com.tav.web.bo.UserSession;
import com.tav.web.bo.ValidationResult;
import com.tav.web.common.CommonConstant;
import com.tav.web.common.CommonFunction;
import com.tav.web.common.ConvertData;
import com.tav.web.common.ErpConstants;
import com.tav.web.common.StringUtil;
import com.tav.web.data.EvaluatePlan6Data;
import com.tav.web.dto.EvaluatePlan6DTO;
import com.tav.web.dto.ImportErrorMessage;
import java.util.Date;
import com.tav.web.dto.SearchCommonFinalDTO;
import com.tav.web.dto.ObjectCommonSearchDTO;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MediaType;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class EvaluatePlan6Controller extends SubBaseController {

    @Autowired
    private EvaluatePlan6Data evaluatePlan6Data;

    @RequestMapping("/" + ErpConstants.RequestMapping.EVALUATE_PLAN6)
    public ModelAndView agent(Model model, HttpServletRequest request) {
        return new ModelAndView("evaluatePlan6");
    }

    @RequestMapping(value = {"/" + ErpConstants.RequestMapping.GET_ALL_EVALUATE_PLAN6}, method = RequestMethod.GET)
    @ResponseBody
    public JsonDataGrid getAll(HttpServletRequest request) {
        try {
            // get info paging
            Integer currentPage = getCurrentPage();
            Integer limit = getTotalRecordPerPage();
            Integer offset = --currentPage * limit;
            JsonDataGrid dataGrid = new JsonDataGrid();
            SearchCommonFinalDTO searchDTO = new SearchCommonFinalDTO();
            searchDTO.setStringKeyWord(request.getParameter("key"));
	if (request.getParameter("listLong1") != null) {
                searchDTO.setListLong1(ConvertData.convertStringToListLong(request.getParameter("listLong1")));
            }
	if (request.getParameter("listLong2") != null) {
                searchDTO.setListLong2(ConvertData.convertStringToListLong(request.getParameter("listLong2")));
            }
            searchDTO.setString1(request.getParameter("string1"));
            searchDTO.setString2(request.getParameter("string2"));            List<EvaluatePlan6DTO> lst = new ArrayList<>();
            Integer totalRecords = 0;
            totalRecords = evaluatePlan6Data.getCount(searchDTO);
            if (totalRecords > 0) {
                lst = evaluatePlan6Data.getAll(searchDTO, offset, limit);
            }
            dataGrid.setCurPage(getCurrentPage());
            dataGrid.setTotalRecords(totalRecords);
            dataGrid.setData(lst);
            return dataGrid;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @RequestMapping(value = "/" + ErpConstants.RequestMapping.GET_EVALUATE_PLAN6_BY_ID, method = RequestMethod.GET)
    public @ResponseBody
    EvaluatePlan6DTO getOneById(HttpServletRequest request) {
        Long id = Long.parseLong(request.getParameter("gid"));
        return evaluatePlan6Data.getOneById(id);
    }

    //add
    @RequestMapping(value = {"/" + ErpConstants.RequestMapping.ADD_EVALUATE_PLAN6}, method = RequestMethod.POST, produces = ErpConstants.LANGUAGE)
    @ResponseBody
    public String addOBJ(@ModelAttribute("evaluatePlan6Form") EvaluatePlan6DTO evaluatePlan6DTO, MultipartHttpServletRequest multipartRequest,
            HttpServletRequest request) throws ParseException {

        JSONObject result;
        String error = validateForm(evaluatePlan6DTO);
        ServiceResult serviceResult;
        if (error != null) {
            return error;
        } else {
            if (!StringUtil.isEmpty(evaluatePlan6DTO.getExpertise_date())) {
                        evaluatePlan6DTO.setExpertise_date(DateUtil.formatDate(evaluatePlan6DTO.getExpertise_date()));
            }
            serviceResult = evaluatePlan6Data.addObj(evaluatePlan6DTO);
            processServiceResult(serviceResult);
            result = new JSONObject(serviceResult);
        }
        return result.toString();
    }

    //update
    @RequestMapping(value = {"/" + ErpConstants.RequestMapping.UPDATE_EVALUATE_PLAN6}, method = RequestMethod.POST, produces = ErpConstants.LANGUAGE)
    @ResponseBody
    public String updateOBJ(@ModelAttribute("evaluatePlan6Form") EvaluatePlan6DTO evaluatePlan6DTO, MultipartHttpServletRequest multipartRequest,
            HttpServletRequest request) throws ParseException {

        JSONObject result;
        String error = validateForm(evaluatePlan6DTO);
        ServiceResult serviceResult;
        if (error != null) {
            return error;
        } else {
            if (!StringUtil.isEmpty(evaluatePlan6DTO.getExpertise_date())) {
                        evaluatePlan6DTO.setExpertise_date(DateUtil.formatDate(evaluatePlan6DTO.getExpertise_date()));
            }
            serviceResult = evaluatePlan6Data.updateBO(evaluatePlan6DTO);
            processServiceResult(serviceResult);
            result = new JSONObject(serviceResult);
        }
        return result.toString();
    }

    //validate
    private String validateForm(EvaluatePlan6DTO cbChaDTO) {
        List<ValidationResult> lsError = new ArrayList<>();
        if (lsError.size() > 0) {
            Gson gson = new Gson();
            return gson.toJson(lsError);
        }
        return null;
    }

    @RequestMapping(value = {"/" + ErpConstants.RequestMapping.DELETE_EVALUATE_PLAN6}, method = RequestMethod.POST,
            produces = "text/html;charset=utf-8")
    public @ResponseBody
    String deleteObj(@ModelAttribute("objectCommonSearchDTO") ObjectCommonSearchDTO objectCommonSearchDTO,
            HttpServletRequest request) {
        HttpSession session = request.getSession();
        ServiceResult serviceResult = evaluatePlan6Data.deleteObj(objectCommonSearchDTO);
        processServiceResult(serviceResult);
        JSONObject result = new JSONObject(serviceResult);
        return result.toString();
    }

}