package com.humanharvest.organz;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.GestureEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.TouchPoint;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

import com.humanharvest.organz.utilities.ReflectionUtils;

public class MultitouchHandler {
    private final List<CurrentTouch> touches = new ArrayList<>();
    private final Pane rootPane;

    public MultitouchHandler(Pane rootPane) {
        this.rootPane = rootPane;
        rootPane.addEventFilter(MouseEvent.ANY, event -> {
            // TODO: Don't ignore events that don't hit the root pane
            if (event.isSynthesized()) {
                if (event.getClickCount() == Integer.MAX_VALUE) {
                    try {
                        ReflectionUtils.setField(event, "clickCount", 1);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    event.consume();
                }
            }
        });

        rootPane.addEventFilter(TouchEvent.ANY, event -> {
            event.consume();

            TouchPoint touchPoint = event.getTouchPoint();
            CurrentTouch currentTouch = getCurrentTouch(touchPoint);
            if (currentTouch.getPane().isPresent()) {
                currentTouch.setCurrentPanePoint(
                        currentTouch.getTransform().transform(touchPoint.getX(), touchPoint.getY()));
            }

            if (event.getEventType() == TouchEvent.TOUCH_PRESSED) {
                currentTouch.setCurrentScreenPoint(new Point2D(touchPoint.getX(), touchPoint.getY()));
                currentTouch.getPane().ifPresent(Node::toFront);
            } else if (event.getEventType() == TouchEvent.TOUCH_RELEASED) {
                if (currentTouch.getPane().isPresent()) {
                    List<CurrentTouch> paneTouches = findPaneTouches(currentTouch.getPane().get());
                    if (paneTouches.size() == 1) {
                        // TODO: Only if not actual element

                        if (true) {
                            handleTouchToMouse(currentTouch, touchPoint);
                        }
                    }
                }
                removeCurrentTouch(touchPoint);
            } else {
                handleCurrentTouch(touchPoint, currentTouch);
            }
        });

        rootPane.addEventFilter(ScrollEvent.ANY, Event::consume);
        rootPane.addEventFilter(GestureEvent.ANY, Event::consume);
        rootPane.addEventFilter(RotateEvent.ANY, Event::consume);

//        rootPane.addEventFilter(Event.ANY, event -> {
//            System.out.println(event);
//        });
    }

    private void handleCurrentTouch(TouchPoint touchPoint, CurrentTouch currentTouch) {
        currentTouch.getPane().ifPresent(pane -> {
            Point2D newTouchPoint = new Point2D(touchPoint.getX(), touchPoint.getY());

            List<CurrentTouch> paneTouches = findPaneTouches(pane);
            if (paneTouches.size() == 1) {
                Point2D delta = newTouchPoint.subtract(currentTouch.getCurrentScreenPoint());
                currentTouch.getTransform().prepend(new Translate(delta.getX(), delta.getY()));
                currentTouch.setCurrentScreenPoint(newTouchPoint);

            } else if (paneTouches.size() == 2) {
                CurrentTouch otherTouch = getOtherTouch(currentTouch, paneTouches);

                double angleDelta = calculateAngleDelta(currentTouch, otherTouch, touchPoint);
                Point2D centre = min(currentTouch.getCurrentScreenPoint(), otherTouch.getCurrentScreenPoint())
                        .add(abs(currentTouch.getCurrentScreenPoint().subtract(otherTouch.getCurrentScreenPoint()))
                                .multiply(0.5));

                if (currentTouch.getLastCentre() != null) {
                    Point2D delta = centre.subtract(currentTouch.getLastCentre());
                    currentTouch.getTransform().prepend(new Translate(delta.getX(), delta.getY()));

                    double scaleDifference =
                            new Point2D(touchPoint.getX(), touchPoint.getY()).distance(
                                    otherTouch.getCurrentScreenPoint()) /
                                    currentTouch.getCurrentScreenPoint().distance(otherTouch.getCurrentScreenPoint());

                    currentTouch.getTransform()
                            .prepend(new Scale(scaleDifference, scaleDifference, centre.getX(), centre.getY()));
                }
                currentTouch.getTransform().prepend(new Rotate(Math.toDegrees(angleDelta), centre.getX(), centre.getY()));

                currentTouch.setLastCentre(centre);
                otherTouch.setLastCentre(centre);
                currentTouch.setCurrentScreenPoint(newTouchPoint);
            }
        });
    }

    private static void handleTouchToMouse(CurrentTouch currentTouch, TouchPoint touchPoint) {
        // TODO: Handle double click
        Node target = (Node)touchPoint.getTarget();
        target.fireEvent(createMouseEvent(currentTouch, touchPoint, MouseEvent.MOUSE_ENTERED_TARGET, false));
        target.fireEvent(createMouseEvent(currentTouch, touchPoint, MouseEvent.MOUSE_MOVED, false));
        target.fireEvent(createMouseEvent(currentTouch, touchPoint, MouseEvent.MOUSE_PRESSED, true));
        target.fireEvent(createMouseEvent(currentTouch, touchPoint, MouseEvent.MOUSE_RELEASED, true));
        target.fireEvent(createMouseEvent(currentTouch, touchPoint, MouseEvent.MOUSE_CLICKED, true));
    }

    private static Event createMouseEvent(CurrentTouch currentTouch, TouchPoint touchPoint,
            EventType<MouseEvent> eventType,
            boolean primaryButton) {
        return new MouseEvent(
                currentTouch,
                touchPoint.getTarget(),
                eventType,
                currentTouch.getCurrentScreenPoint().getX(),
                currentTouch.getCurrentScreenPoint().getY(),
                currentTouch.getCurrentScreenPoint().getX(),
                currentTouch.getCurrentScreenPoint().getY(),
                primaryButton ? MouseButton.PRIMARY : MouseButton.NONE,
                Integer.MAX_VALUE,
                false,
                false,
                false,
                false,
                primaryButton,
                false,
                false,
                true,
                false,
                true,
                touchPoint.getPickResult());
    }

    private static double calculateAngleDelta(
            CurrentTouch currentTouch,
            CurrentTouch otherTouch,
            TouchPoint touchPoint) {
        double oldAngle = calculateAngle(currentTouch.getCurrentScreenPoint(),
                otherTouch.getCurrentScreenPoint());
        double newAngle = calculateAngle(
                new Point2D(touchPoint.getX(), touchPoint.getY()),
                otherTouch.getCurrentScreenPoint());
        return newAngle - oldAngle;
    }

    private static CurrentTouch getOtherTouch(CurrentTouch currentTouch, List<CurrentTouch> paneTouches) {
        if (Objects.equals(paneTouches.get(0), currentTouch)) {
            return paneTouches.get(1);
        } else {
            return paneTouches.get(0);
        }
    }

    private static Point2D abs(Point2D point2D) {
        return new Point2D(Math.abs(point2D.getX()), Math.abs(point2D.getY()));
    }

    private static Point2D min(Point2D p1, Point2D p2) {
        return new Point2D(Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()));
    }

    private static double calculateAngle(Point2D point1, Point2D point2) {
        return Math.atan2(point2.getY() - point1.getY(), point2.getX() - point1.getX());
    }

    private List<CurrentTouch> findPaneTouches(Pane pane) {
        List<CurrentTouch> results = new ArrayList<>();
        for (CurrentTouch currentTouch : touches) {
            if (currentTouch != null) {
                Optional<Pane> touchPane = currentTouch.getPane();
                if (touchPane.isPresent() && Objects.equals(touchPane.get(), pane)) {
                    results.add(currentTouch);
                }
            }
        }
        return results;
    }

    private Optional<Pane> findPane(TouchPoint touchPoint) {
        Node intersectNode = touchPoint.getPickResult().getIntersectedNode();
        while (!Objects.equals(intersectNode.getParent(), rootPane)) {
            intersectNode = intersectNode.getParent();
            if (intersectNode == null) {
                return Optional.empty();
            }
        }
        return Optional.of((Pane)intersectNode);
    }

    private CurrentTouch getCurrentTouch(TouchPoint touchPoint) {
        while (touchPoint.getId() >= touches.size()) {
            touches.add(null);
        }

        CurrentTouch currentTouch = touches.get(touchPoint.getId());
        if (currentTouch == null) {
            currentTouch = new CurrentTouch(findPane(touchPoint));
            touches.set(touchPoint.getId(), currentTouch);
        }

        return currentTouch;
    }

    private void removeCurrentTouch(TouchPoint touchPoint) {
        touches.set(touchPoint.getId(), null);
    }

    private static class CurrentTouch {
        private final Optional<Pane> pane;
        private Point2D currentScreenPoint;
        private Point2D currentPanePoint;
        private Affine transform;
        private Point2D lastCentre;

        public CurrentTouch(Optional<Pane> pane) {
            this.pane = pane;
            setupInitialTransforms();
        }

        private void setupInitialTransforms() {
            pane.ifPresent(pane -> {
                ObservableList<Transform> transforms = pane.getTransforms();
                if (transforms.isEmpty()) {
                    transform = new Affine();
                    transforms.add(getTransform());
                } else if (transforms.size() == 1) {
                    transform = (Affine)transforms.get(0);
                } else {
                    throw new RuntimeException();
                }
            });
        }

        public Optional<Pane> getPane() {
            return pane;
        }

        public Point2D getCurrentScreenPoint() {
            return currentScreenPoint;
        }

        public void setCurrentScreenPoint(Point2D currentScreenPoint) {
            this.currentScreenPoint = currentScreenPoint;
        }

        public Point2D getCurrentPanePoint() {
            return currentPanePoint;
        }

        public void setCurrentPanePoint(Point2D currentPanePoint) {
            this.currentPanePoint = currentPanePoint;
        }

        public Affine getTransform() {
            return transform;
        }

        public Point2D getLastCentre() {
            return lastCentre;
        }

        public void setLastCentre(Point2D lastCentre) {
            this.lastCentre = lastCentre;
        }
    }
}
