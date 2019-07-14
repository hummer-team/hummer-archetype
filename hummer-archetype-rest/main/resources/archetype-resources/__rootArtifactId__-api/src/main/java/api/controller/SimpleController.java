package ${package}.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hujiang.basic.framework.core.util.JsonUtil;
import com.hujiang.basic.framework.dao.model.PageRequest;
import com.hujiang.basic.framework.dao.model.PageResponse;
import com.hujiang.basic.framework.rest.model.DataResult;
import com.hujiang.basic.framework.rest.validation.annotation.HibernateValidatorBased;
import ${package}.service.facade.IUser;
import ${package}.support.model.dto.UserRequest;
import ${package}.support.model.dto.UserResponse;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value="/v1/simple")
public class SimpleController {

	@Autowired
	IUser userService;

	@RequestMapping(value = "/{userid}", method = RequestMethod.GET)
	public DataResult<UserResponse> getUserbyid(@PathVariable String userid) {
        return new DataResult<UserResponse>(userService.load(userid));
	}

}