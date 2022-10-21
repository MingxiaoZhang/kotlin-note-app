import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.stage.Stage

class PaneFactory(private val model: Model, stage: Stage) {
    val notePaneFactory = NotePaneFactory(model, stage)

    // Returns VBox for notes in list view
    private fun buildList() : Pane{
        val noteTextList = TextArea().apply{
            prefHeight = 42.0
            HBox.setHgrow(this, Priority.ALWAYS)
        }
        val addBtnList = Button("Create").apply{
            prefWidth = 75.0
            prefHeight = 42.0
            HBox.setHgrow(this, Priority.NEVER)
        }
        val addList = HBox(noteTextList, addBtnList).apply{
            prefHeight = 62.0
            padding = Insets(10.0)
            spacing = 10.0
            background = Background(
                BackgroundFill(Color.LIGHTSALMON, CornerRadii(10.0), null))
        }
        val listPane = VBox(addList).apply{
            spacing = 10.0
            padding = Insets(10.0)
        }
        addBtnList.onAction = EventHandler {
            model.addNote(Note(noteTextList.text, false))
        }
        for (i in model.notes){
            addNote(listPane, i)
        }
        return listPane
    }

    // Returns TilePane for grid view
    private fun buildGrid() : Pane{
        val noteTextGrid = TextArea().apply {
            isWrapText = true
        }
        val addBtnGrid = Button("Create").apply {
            prefWidth = 205.0
        }
        val addGrid = VBox(noteTextGrid, addBtnGrid).apply{
            maxWidth = 225.0
            maxHeight = 225.0
            padding = Insets(10.0)
            spacing = 10.0
            alignment = Pos.CENTER
            background = Background(
                BackgroundFill(Color.LIGHTSALMON, CornerRadii(10.0), null))
        }
        val gridPane = TilePane(addGrid).apply{
            padding = Insets(10.0)
            hgap = 10.0
            vgap = 10.0
        }
        addBtnGrid.onAction = EventHandler {
            model.addNote(Note(noteTextGrid.text, false))
        }
        for (i in model.notes){
            addNote(gridPane, i)
        }
        return gridPane
    }

    private fun addNote(pane: Pane, note: Note){
        val notePane = notePaneFactory.getNotePane(note, model.viewMode)
        notePane.onMouseClicked = EventHandler {
            e -> if (e.clickCount == 2 && !model.isEditing && !note.archived) {
                model.beginEdit(note, notePane)
                notePane.children[0] = TextArea(note.text).apply {
                    isWrapText = true
                    textProperty().addListener {
                            _,_,new -> model.editText.text = new
                    }
                }
            }
        }
        pane.children.add(notePane)
    }

    // Returns pane based on required view
    fun displayNotes(viewMode: PaneMode) : Pane {
        return if (viewMode == PaneMode.LIST) {
            buildList()
        } else {
            buildGrid()
        }
    }
}

class NotePaneFactory (private val model: Model, private val stage: Stage) {
    fun setBackground(note: Note, pane: Pane) {
        if (note.archived) {
            pane.background = Background(
                BackgroundFill(Color.LIGHTGRAY, CornerRadii(10.0), null))
        } else {
            pane.background = Background(
                BackgroundFill(Color.LIGHTYELLOW, CornerRadii(10.0), null))
        }
    }

    // Returns a pane for the given note based on view mode
    fun getNotePane(note:Note, viewMode: PaneMode) : Pane{
        if (viewMode == PaneMode.GRID) {
            val archive2 = CheckBox("Archived")
            val text2 = Label(note.text).apply{
                isWrapText = true
            }
            var noteTile = VBox(text2, VBox().apply{VBox.setVgrow(this, Priority.ALWAYS)}, archive2).apply {
                maxWidth = 225.0
                maxHeight = 225.0
                padding = Insets(10.0)
                spacing = 10.0
                background = Background(
                    BackgroundFill(Color.LIGHTYELLOW, CornerRadii(10.0), null))
            }
            noteTile.managedProperty().bind(noteTile.visibleProperty())
            // Set initial visibility
            if (note.archived){
                archive2.isSelected = true
                if(!model.showArchived) {
                    noteTile.isVisible = false
                }
                noteTile.background = Background(
                    BackgroundFill(Color.LIGHTGRAY, CornerRadii(10.0), null))
            }
            archive2.selectedProperty().addListener {
                    _,_, newValue ->
                model.setArchive(note, noteTile, newValue)
            }
            return noteTile
        } else {
            val archive = CheckBox("Archived").apply {
                maxWidth = 140.0
                minWidth = 80.0
            }
            val text = Text(note.text)
            val noteBlock = HBox(text, archive).apply{
                spacing = 10.0
                padding = Insets(10.0)
                background = Background(
                    BackgroundFill(Color.LIGHTYELLOW, CornerRadii(10.0), null))
            }
            // Allow maximum wrapping width
            text.wrappingWidthProperty().bind(stage.widthProperty().subtract(archive.maxWidth))
            noteBlock.managedProperty().bind(noteBlock.visibleProperty())
            if (note.archived){
                archive.isSelected = true
                if(!model.showArchived) {
                    noteBlock.isVisible = false
                }
                noteBlock.background = Background(
                    BackgroundFill(Color.LIGHTGRAY, CornerRadii(10.0), null))
            }
            // Listener for checkbox
            archive.selectedProperty().addListener {
                    _,_, newValue ->
                model.setArchive(note, noteBlock, newValue)
            }
            return noteBlock
        }
    }
}


