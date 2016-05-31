package jgnash.uifx.control;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * Doughnut Chart implementation.
 *
 * @author Craig Cavanaugh
 */
public class DoughnutChart extends PieChart {

    private static final int RING_WIDTH = 3;

    private final Circle hole;

    private final Text titleText;

    private final StringProperty centerTitle = new SimpleStringProperty();

    @SuppressWarnings("unused")
    public DoughnutChart() {
        this(FXCollections.observableArrayList());
    }

    private DoughnutChart(final ObservableList<Data> data) {
        super(data);

        hole = new Circle();
        hole.setStyle("-fx-fill: -fx-background");

        titleText = new Text();
        titleText.setStyle("-fx-font-size: 1.4em");
        titleText.setTextAlignment(TextAlignment.JUSTIFY);
        titleText.textProperty().bind(centerTitle);
    }

    public StringProperty centerTitleProperty() {
        return centerTitle;
    }

    @Override
    protected void layoutChartChildren(final double top, final double left, final double contentWidth,
                                       final double contentHeight) {

        super.layoutChartChildren(top, left, contentWidth, contentHeight);

        installContent();
        updateLayout();
    }

    private void installContent() {
        if (!getData().isEmpty()) {
            final Node node = getData().get(0).getNode();
            if (node.getParent() instanceof Pane) {
                final Pane parent = (Pane) node.getParent();

                if (!parent.getChildren().contains(hole)) {
                    parent.getChildren().add(hole);
                    parent.getChildren().add(titleText);
                }
            }
        }
    }

    private void updateLayout() {

        // Determine maximums and minimums, make use of available processors
        final double minX = getData().parallelStream().mapToDouble(value -> value.getNode().getBoundsInParent()
                .getMinX()).min().orElse(0);

        final double minY = getData().parallelStream().mapToDouble(value -> value.getNode().getBoundsInParent()
                .getMinY()).min().orElse(0);

        final double maxX = getData().parallelStream().mapToDouble(value -> value.getNode().getBoundsInParent()
                .getMaxX()).max().orElse(Double.MAX_VALUE);

        final double maxY = getData().parallelStream().mapToDouble(value -> value.getNode().getBoundsInParent()
                .getMaxY()).max().orElse(Double.MAX_VALUE);

        // center the hole and size
        hole.setCenterX(minX + (maxX - minX) / 2);
        hole.setCenterY(minY + (maxY - minY) / 2);
        hole.setRadius((maxX - minX) / RING_WIDTH);

        // center the title
        titleText.setX((minX + (maxX - minX) / 2) - titleText.getLayoutBounds().getWidth() / 2);
        titleText.setY(minY + (maxY - minY) / 2);
    }
}
