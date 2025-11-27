package org.yaojiu.supermarket.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import org.yaojiu.supermarket.entity.Good;
import org.yaojiu.supermarket.entity.GoodQueryObject;
import org.yaojiu.supermarket.entity.Result;
import org.yaojiu.supermarket.service.GoodService;
import org.yaojiu.supermarket.utils.DynamicQueryUtils;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/good")
public class GoodController {
    @Resource
    private GoodService goodService;

    @PutMapping(value = "/add")
    public Result addGood(@Valid @RequestBody Good good) {
        if (goodService.save(good)) return Result.success().resetMsg("添加成功");
        return Result.fail().resetMsg("添加失败");
    }

    @DeleteMapping(value = "/{id}/del")
    public Result deleteGood(@PathVariable(value = "id") Integer id){
        if (goodService.removeById(id)) return Result.success().resetMsg("删除成功");
        return Result.fail().resetMsg("删除失败");
    }
    @PutMapping(value = "/{id}/update")
    public Result updateGood(@PathVariable(value = "id",required = true) Integer id, @Valid @RequestBody Good good){
        if (goodService.updateById(good)) return Result.success().resetMsg("修改成功");
        return Result.fail().resetMsg("修改失败");
    }
    @GetMapping(value = "/{id}")
    public Result getGood(@PathVariable(value = "id",required = true) Integer id){
        Good good = goodService.getById(id);
        if (good != null) return Result.success().resetMsg("查询成功").resetData(good);
        return Result.fail().resetMsg("未找到该商品");
    }
    @GetMapping(value = "/list")
    public Result listGood(@Valid GoodQueryObject queryObject, @RequestParam(value = "page",required = false,defaultValue = "1") Integer page){
        @SuppressWarnings("unchecked")
        Page<Good> pages = goodService.page(new Page<Good>(page, 10), (Wrapper<Good>) DynamicQueryUtils.getQueryWrapper(queryObject));
        return Result.success().resetMsg("查询成功").resetData(pages);
    }
}
