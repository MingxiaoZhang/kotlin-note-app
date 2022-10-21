
class Controller(private val model : Model) {
    fun setGrid() {
        model.setView(PaneMode.GRID)
    }
    fun setList() {
        model.setView(PaneMode.LIST)
    }
    fun toggleArchived() {
        model.toggleArchived()
    }
    fun setOrder(order: Orders) {
        model.setOrders(order)
    }
    fun clearNotes() {
        model.clearNotes()
    }
}