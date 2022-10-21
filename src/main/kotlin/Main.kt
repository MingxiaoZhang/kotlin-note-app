import javafx.application.Application
import javafx.stage.Stage

class Main : Application() {
    // main entry point for an JavaFX application
    // this is the only base method that needs to be implemented
    override fun start(stage: Stage) {
        val model = Model()
        val controller = Controller(model)
        val view = View(model, stage)
        view.addListener(controller)
    }


}