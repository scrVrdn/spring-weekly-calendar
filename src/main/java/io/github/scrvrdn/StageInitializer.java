package io.github.scrvrdn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import io.github.scrvrdn.Main.StageReadyEvent;
import io.github.scrvrdn.controller.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {
    @Value("classpath:main.fxml")
    private Resource fxmlResource;
   
    @Value("classpath:style.css")
    private Resource styleResource;

    @Autowired
    private ApplicationContext context;
    
    private String appTitle;

    public StageInitializer(@Value("${spring.application.title}") String appTitle) {
        this.appTitle = appTitle;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(fxmlResource.getURL());
            loader.setControllerFactory(context::getBean);
            Parent parent = loader.load();            
            
            Scene scene = new Scene(parent);
            scene.getStylesheets().add(styleResource.getURL().toExternalForm());

            Stage stage = event.getStage();

            stage.setScene(scene);
            stage.setTitle(appTitle);            
            stage.show();
            MainController controller = loader.getController();
            controller.setStage(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
