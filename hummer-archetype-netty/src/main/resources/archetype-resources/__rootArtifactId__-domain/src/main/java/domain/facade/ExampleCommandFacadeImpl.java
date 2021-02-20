package ${package}.domain.facade;

import ${package}.facade.ExampleCommandFacade;

public class ExampleCommandFacadeImpl implements ExampleCommandFacade {
    @Override
    public String hellword(String args) {
        return "hellword #2";
    }
}
