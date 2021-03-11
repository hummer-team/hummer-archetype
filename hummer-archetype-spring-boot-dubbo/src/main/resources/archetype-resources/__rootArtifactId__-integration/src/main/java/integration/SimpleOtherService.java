package ${package}.integration;

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
}