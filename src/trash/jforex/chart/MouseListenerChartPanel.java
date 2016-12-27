package trash.jforex.chart;

import com.dukascopy.api.chart.mouse.ChartPanelMouseAdapter;
import com.dukascopy.api.chart.mouse.IChartPanelMouseEvent;
import com.dukascopy.api.drawings.IChartDependentChartObject;
import com.dukascopy.api.drawings.IPolyLineChartObject;

/**
 * The chart panel mouse listener, to draw several objects.
 *
 * @author Miquel Sas
 */
public class MouseListenerChartPanel extends ChartPanelMouseAdapter {

	/** The chart manager. */
	private ChartManager chartManager;
	/** The object that will be drawn. */
	private ChartObject chartObject;
	/** The object to be drawn. */
	private IChartDependentChartObject drawingObject;
	/** The point index. */
	private int pointIndex = -1;
	/** The maximum number of points(can be 0). */
	private int maxPoints = 0;

	/**
	 * Constructor.
	 */
	public MouseListenerChartPanel(ChartManager chartManager) {
		super();
		this.chartManager = chartManager;
	}

	/**
	 * Creates the drawing object depending on the chart object to create.
	 * 
	 * @return The drawing object
	 */
	private IChartDependentChartObject createDrawingObject() {
		IChartDependentChartObject object = null;
		switch (chartObject) {
		case ShortLine:
			object = chartManager.getChartObjectFactory().createShortLine();
			break;
		case LongLine:
			object = chartManager.getChartObjectFactory().createLongLine();
			break;
		case RayLine:
			object = chartManager.getChartObjectFactory().createRayLine();
			break;
		case PolyLine:
			object = chartManager.getChartObjectFactory().createPolyLine();
			break;
		case HorizontalLine:
			object = chartManager.getChartObjectFactory().createHorizontalLine();
			break;
		case VerticalLine:
			object = chartManager.getChartObjectFactory().createVerticalLine();
			break;
		case Rectangle:
			object = chartManager.getChartObjectFactory().createRectangle();
			break;
		case Ellipse:
			object = chartManager.getChartObjectFactory().createEllipse();
			break;
		case Triangle:
			object = chartManager.getChartObjectFactory().createTriangle();
			break;
		case FibonacciRetracements:
			object = chartManager.getChartObjectFactory().createFiboRetracement();
			break;
		case FibonacciExpansion:
			object = chartManager.getChartObjectFactory().createFiboExpansion();
			break;
		}
		// Reset the point index.
		pointIndex = -1;
		maxPoints = object.getPointsCount();
		return object;
	}

	/**
	 * Clear drawing.
	 */
	private void clearDrawing() {
		chartObject = null;
		drawingObject = null;
		pointIndex = -1;
		maxPoints = 0;
	}

	/**
	 * Check if drawing should continue.
	 * 
	 * @return A boolean
	 */
	private boolean checkContinue() {
		if (maxPoints == 0) {
			return true;
		}
		if (pointIndex < maxPoints - 1) {
			return true;
		}
		return false;
	}
	
	/**
	 * Adds a point, only if the drawing object is an instance of objects with an undefined number of points.
	 * @param e The IChartPanelMouseEvent
	 */
	private void addPoint(IChartPanelMouseEvent e) {
		if (drawingObject instanceof IPolyLineChartObject) {
			IPolyLineChartObject polyLine = (IPolyLineChartObject)drawingObject;
			polyLine.addNewPoint(e.getTime(), e.getPrice());
		}
	}

	/**
	 * Draw and refresh the point.
	 * 
	 * @param pointIndex The point index to draw and refresh
	 * @param e The IChartPanelMouseEvent
	 */
	private void drawPoint(int pointIndex, IChartPanelMouseEvent e) {
		drawingObject.setTime(pointIndex, e.getTime());
		drawingObject.setPrice(pointIndex, e.getPrice());
		chartManager.getChart().repaint();
	}

	/**
	 * Respond to the mouse clicked event.
	 */
	public void mousePressed(IChartPanelMouseEvent e) {
		if (chartObject != null) {
			if (drawingObject == null) {
				drawingObject = createDrawingObject();
				addPoint(e);
				chartManager.getChart().add(drawingObject);
			}
			pointIndex++;
			drawPoint(pointIndex, e);
			addPoint(e);
			if (!checkContinue()) {
				clearDrawing();
			}
		}
	}

	/**
	 * Respond to the mouse moved event.
	 */
	public void mouseMoved(IChartPanelMouseEvent e) {
		if (chartObject != null) {
			if (drawingObject != null) {
				drawPoint(pointIndex + 1, e);
			}
		}
	}

	/**
	 * Respond to the mouse clicked event.
	 */
	public void mouseClicked(IChartPanelMouseEvent e) {
		if (chartObject != null) {
			if (drawingObject != null) {
				if (maxPoints == 0) {
					if (e.getSourceEvent().getClickCount() == 2) {
						drawPoint(pointIndex + 1, e);
						clearDrawing();
					}
				}
			}
		}
	}
	
	/**
	 * Set the chart object to be drawn.
	 * 
	 * @param chartObject The chart object to be drawn.
	 */
	public void setChartObject(ChartObject chartObject) {
		this.chartObject = chartObject;
	}

}