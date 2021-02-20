package ${package}.api.main;

import io.elves.core.ElvesApplication;
import io.elves.core.ElvesBootStart;
import io.elves.http.server.HttpServer;

@ElvesApplication(scanPackage = "${package}", bootServer = HttpServer.class)
public class ApplicationStart {
    public static void main(String[] args){
        ElvesBootStart.run(ApplicationStart.class, args);
    }
}
