import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.stage.Stage

class View(private val model: Model, val stage: Stage) {
    private val gridBtn = Button("Grid")
    private val listBtn = Button("List")
    private val showArch = CheckBox()
    private val selectOrder = ChoiceBox(FXCollections.observableArrayList(Orders.ASC.text, Orders.DESC.text, Orders.ALPH.text))
    private val clearBtn = Button("Clear")
    var status = Label("${model.notes.size} notes, ${model.numActive} of which are active")
    var paneFactory = PaneFactory(model, stage)
    var notes = paneFactory.displayNotes(model.viewMode)
    var root = BorderPane()
    init {
        // Initial disabled button
        if (model.viewMode == PaneMode.GRID) {
            gridBtn.isDisable = true
        } else {
            listBtn.isDisable = true
        }
        // Set initial order selection
        selectOrder.value = Orders.ASC.text
        // Add view to model
        model.views.add(this)
        // Set root contents
        root.top = displayTools()
        root.center = ScrollPane(notes).apply{
            isFitToWidth = true
            hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
        }
        root.bottom = Pane(status).apply{
            VBox.setVgrow(this, Priority.ALWAYS)
        }
        // Show stage
        stage.apply {
            title = "CS349 - A1 Notes - m468zhan"
            scene = Scene(root , 800.0, 600.0).apply { fill = Color.BLUE }
        }.show()
    }

    // Change visibility or all archived notes
    fun toggleArchived() {
        model.notes.forEachIndexed {
                index, note -> if(note.archived) {
            notes.children[index + 1].isVisible = model.showArchived
        }
        }
    }

    // Update view mode - reload all notes according to model
    fun updateNotes() {
        notes = paneFactory.displayNotes(model.viewMode)
        gridBtn.isDisable = (model.viewMode == PaneMode.GRID)
        listBtn.isDisable = (model.viewMode == PaneMode.LIST)
        root.center = ScrollPane(notes).apply{
            isFitToWidth = true
            hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
        }
        displayStatus()
    }

    // Change background for archived note
    fun updateArchived(note: Note, pane : Pane) {
        paneFactory.notePaneFactory.setBackground(note, pane)
        displayStatus()
        if (model.editText == note) {
            model.editNote()
            root.onMouseClicked = null
        }
    }

    // Display toolbar
    private fun displayTools() : ToolBar {
        val displayGroup = applyStyle(HBox(Label("View:"), applyStyle(this.listBtn), applyStyle(this.gridBtn)))

        val archiveGroup = applyStyle(HBox(Label("Show archived:"), this.showArch))

        val orderGroup = applyStyle(HBox(Label("Order by:"), this.selectOrder))

        return ToolBar(displayGroup, archiveGroup, orderGroup,
            HBox().apply{HBox.setHgrow(this, Priority.ALWAYS)},
            applyStyle(clearBtn)).apply {
                maxWidth = Double.MAX_VALUE
            }
    }

    // Applies style to control
    private fun applyStyle(tool : Control) : Control{
        tool.apply{
            maxHeight = 10.0
            prefWidth = 50.0 }
        return tool
    }

    // Applies style to HBox
    private fun applyStyle(hbox: HBox) : HBox {
        return hbox.apply{
            alignment = Pos.CENTER
            spacing = 10.0
            padding = Insets(10.0)}
    }

    private fun displayStatus() {
        status.text = "${model.notes.size} note${
            if(model.notes.size == 1) "" else "s"
        }, ${model.numActive} of which ${
            if(model.numActive == 1) "is" else "are"
        } active"
    }

    // Sets event handlers and listeners to controls
    fun addListener(controller: Controller) {
        this.gridBtn.onAction = EventHandler{
            controller.setGrid()
        }
        this.listBtn.onAction = EventHandler{
            listBtn.isDisable = true
            controller.setList()
        }
        this.showArch.selectedProperty().addListener{
            _,_,_ -> controller.toggleArchived()
        }
        this.selectOrder.selectionModel.selectedItemProperty().addListener {
                _, _, newValue ->
            Orders.fromText(newValue)?.let { controller.setOrder(it) }
        }
        this.clearBtn.onAction = EventHandler {
            controller.clearNotes()
        }
    }
    fun addClickListener(notePane : Pane) {
        println(notePane.layoutX + notePane.width)
        println(notePane.layoutY + notePane.height)
        root.onMouseClicked = EventHandler {
            e ->
            if (e.clickCount == 1
            ) {
                model.editNote()
                root.onMouseClicked = null
            }

        }
    }
}

enum class Orders(val text: String) {
    ASC("Length (asc)"), DESC("Length (desc)"), ALPH("Text (asc)");

    // For reverse lookup
    companion object {
        private val mapping = values().associateBy(Orders::text)
        fun fromText(text: String) = mapping[text]
    }
}

