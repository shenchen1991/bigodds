package com.shenchen.odds.controller;
import com.shenchen.odds.model.BigOddsYzResult;
import com.shenchen.odds.service.IOddsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/odds")
public class OddsController {
	private static Logger log=LoggerFactory.getLogger(OddsController.class);
	 @Resource  
	 private IOddsService oddsService;

    @RequestMapping(value="/test",method=RequestMethod.GET)
    public String test(@RequestParam String inputDateStr){
        oddsService.insertKellyOdds(inputDateStr);
        return "DOWNLOADING";
    }

    @RequestMapping(value="/listAll",method=RequestMethod.GET)
    public String listAll(){
        oddsService.listAll();
        return "DOWNLOADING";
    }

    @RequestMapping(value="/InsertOddsyz",method=RequestMethod.GET)
    public String test1(@RequestParam String inputDateStr){
        oddsService.InsertOddsyz(inputDateStr);
        return "DOWNLOADING";
    }

    @RequestMapping(value="/sumYaZhou",method=RequestMethod.GET)
    public String sumYaZhou(){
        oddsService.sumYaZhou();
        return "DOWNLOADING";
    }

    @RequestMapping(value="/jisuangv",method=RequestMethod.GET)
    public String jisuangv(){
        oddsService.guilv();
        return "DOWNLOADING";
    }


    @RequestMapping(value="/getLastYz",method=RequestMethod.GET)
    @ResponseBody
    public List<BigOddsYzResult> getLastYz(){
        return oddsService.getLastYz();
    }

    @RequestMapping(value="/lastYzModel",method=RequestMethod.GET)
    @ResponseBody
    public List<BigOddsYzResult> lastYzModel(){
        return oddsService.lastYzModel();
    }

}  