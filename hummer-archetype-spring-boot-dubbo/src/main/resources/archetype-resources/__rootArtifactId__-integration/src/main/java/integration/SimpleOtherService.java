package ${package}.integration;

import comm.hummer.simple.common.facade.HelloService;
import comm.hummer.simple.common.module.SimpleDubboDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * this class impl call depend remote service
 */
@Component
public class SimpleOtherService {

    @Autowired(required = false)
    private HelloService helloService;

    public int add (int a,int b){
        return helloService.add(a,b);
    }

    public int add2(SimpleDubboDto dto){
        return helloService.add2(dto);
    }
}