package app.view

import javafx.scene.Node
import javafx.scene.control.TextArea
import javafx.scene.layout.Pane

internal open class NodeStyleClass(val name: String)
internal class InteractivePaneStyleClass(name: String) : NodeStyleClass(name)
internal class TextAreaStyleClass(name: String) : NodeStyleClass(name)

// Interactive Pane Style Classes
internal val P_ENABLE = InteractivePaneStyleClass("interactive-pane-enable")
internal val P_DISABLE = InteractivePaneStyleClass("interactive-pane-disable")
internal val P_SUCCESS = InteractivePaneStyleClass("interactive-pane-success")
internal val P_PROCESSING = InteractivePaneStyleClass("interactive-pane-processing")
internal val P_MARKED = InteractivePaneStyleClass("interactive-pane-mark")
internal val P_WARNING = InteractivePaneStyleClass("interactive-pane-warning")

internal val ALL_INTERACTIVE_PANE_CLASS = setOf(
        P_ENABLE, P_DISABLE, P_SUCCESS,
        P_PROCESSING, P_MARKED, P_WARNING
)

// Text Area Style Classes

internal val T_WARNING = TextAreaStyleClass("textarea-warning")
internal val T_SUCCESS = TextAreaStyleClass("textarea-success")

internal val ALL_TEXTAREA_CLASS = setOf(T_SUCCESS, T_WARNING)

// Functions
internal fun Node.setStyleClass(targets: Set<NodeStyleClass>, removal: Set<NodeStyleClass>) {
    this.styleClass.removeAll(removal.map { it.name })
    this.styleClass.addAll(targets.map { it.name })
}

internal fun Pane.setStyleClass(
        target: InteractivePaneStyleClass,
        removal: Set<InteractivePaneStyleClass> = ALL_INTERACTIVE_PANE_CLASS
) = this.setStyleClass(setOf(target), removal)

internal fun TextArea.setStyleClass(
        target: TextAreaStyleClass,
        removal: Set<TextAreaStyleClass> = ALL_TEXTAREA_CLASS
) = this.setStyleClass(setOf(target), removal)
