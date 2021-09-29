package com.kubernetes.demooperator;

import com.kubernetes.demooperator.service.handlers.AddHandler;
import com.kubernetes.demooperator.service.CatWatcher;
import com.kubernetes.demooperator.service.handlers.DeleteHandler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoOperatorApplication implements CommandLineRunner {

    private final CatWatcher catWatcher;
    private final AddHandler addHandler;
    private final DeleteHandler deleteHandler;

    public DemoOperatorApplication(CatWatcher catWatcher, AddHandler addHandler, DeleteHandler deleteHandler) {
        this.catWatcher = catWatcher;
        this.addHandler = addHandler;
        this.deleteHandler = deleteHandler;
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoOperatorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        catWatcher.registerHandler(addHandler);
        catWatcher.registerHandler(deleteHandler);
        catWatcher.watch();
    }
}
