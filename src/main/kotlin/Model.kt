import javafx.scene.layout.Pane

class Model {
    var views = ArrayList<View>()
    var notes = ArrayList<Note>(listOf(
        Note("test", true),
        Note("test0", false),
        Note("test1", true),
        Note("more tests", false)
    ))
    var viewMode = PaneMode.LIST
    var showArchived = false
    var order = Orders.ASC
    var numActive = 2
    var isEditing = false
    var editText = Note("", true)

    fun setView(mode : PaneMode) {
        viewMode = mode
        // Update view
        views.forEach{
            it.updateNotes()
        }
    }

    fun toggleArchived() {
        showArchived = !showArchived
        views.forEach{
            it.toggleArchived()
        }
    }

    fun setOrders(orders: Orders) {
        order = orders
        // Sort by order
        sortNotes()
        views.forEach{
            it.updateNotes()
        }
    }

    fun addNote(note: Note) {
        notes.add(note)
        numActive++
        // Sort after insertion
        sortNotes()
        views.forEach{
            it.updateNotes()
        }
    }

    fun beginEdit(note: Note, notePane: Pane) {
        isEditing = true
        editText = note
        views.forEach{
            it.addClickListener(notePane)
        }
    }


    fun editNote() {
        sortNotes()
        isEditing = false
        views.forEach{
            it.updateNotes()
        }
    }

    private fun sortNotes() {
        when(order){
            Orders.ASC -> notes.sortBy{it.text.length}
            Orders.DESC -> notes.sortBy{-it.text.length}
            Orders.ALPH -> notes.sortBy{it.text}
        }
    }

    fun setArchive(note : Note, notePane : Pane, archived : Boolean) {
        note.archived = archived
        if (archived) {
            numActive--
        } else {
            numActive++
        }
        if (!showArchived) {
            notePane.isVisible = !archived
        }
        views.forEach{
            it.updateArchived(note, notePane)
        }
    }

    fun clearNotes() {
        notes.clear()
        numActive = 0
        views.forEach{
            it.updateNotes()
        }
    }
}

data class Note(var text : String, var archived : Boolean)

enum class PaneMode{
    GRID, LIST
}