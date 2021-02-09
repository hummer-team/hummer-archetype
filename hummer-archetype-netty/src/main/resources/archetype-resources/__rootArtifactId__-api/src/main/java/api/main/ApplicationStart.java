package ${package}.api.main;

import io.elves.core.ElvesBootStart;
import io.elves.http.server.HttpServer;

public class ApplicationStart {
    public static void main(String[] args){
        ElvesBootStart.run(HttpServer.class, args);
    }
}
