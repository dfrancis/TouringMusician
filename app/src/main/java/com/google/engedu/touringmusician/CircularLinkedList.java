/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.touringmusician;


import android.graphics.Point;
import android.util.Log;

import java.util.Iterator;

public class CircularLinkedList implements Iterable<Point> {

    private class Node {
        Point point;
        Node prev, next;

        Node(Point p) {
            point = p;
            next = null;
            prev = null;
        }
    }

    Node head = null;

    public void insertBeginning(Point p) {
        Node newNode = new Node(p);

        if (head == null) {
            newNode.next = newNode;
            newNode.prev = newNode;
        }
        else {
            newNode.next = head;
            newNode.prev = head.prev;
            head.prev.next = newNode;
            head.prev = newNode;
        }
        head = newNode;
    }

    private float distanceBetween(Point from, Point to) {
        return (float) Math.sqrt(Math.pow(from.y-to.y, 2) + Math.pow(from.x-to.x, 2));
    }

    public float totalDistance() {
        float total = 0;

        Iterator<Point> iter = this.iterator();
        Point p = null;
        Point prev = null;
        while (iter.hasNext()) {
            prev = p;
            p = iter.next();
            if (prev != null) {
                total += distanceBetween(prev, p);
            }
        }
        return total;
    }

    private void insertAfter(Point newPt, Point afterPt) {
        Node newNode = new Node(newPt);
        Node afterNode = head;
        Log.d("TourM", "insertAfter start");
        while ((afterNode != null) && (afterNode.next != head)) {
            if (afterNode.point == afterPt) {
                break;
            }
            afterNode = afterNode.next;
        }

        if (afterNode == null) {
            newNode.next = newNode;
            newNode.prev = newNode;
            head = newNode;
        }
        else {
            newNode.next = afterNode.next;
            newNode.prev = afterNode;
            afterNode.next.prev = newNode;
            afterNode.next = newNode;
        }
        Log.d("TourM", "insertAfter start");
    }

    public void insertNearest(Point p) {
        Iterator<Point> iter = this.iterator();
        Point closestPoint = null;
        Point nextPoint = null;
        float closestDist = 0;
        Log.d("TourM", "insertNearest start");
        while (iter.hasNext()) {
            nextPoint = iter.next();
            float nextDist = distanceBetween(nextPoint, p);
            if ((closestPoint == null) || (nextDist < closestDist)) {
                closestPoint = nextPoint;
                closestDist = nextDist;
            }
        }
        if (closestPoint == null) {
            insertBeginning(p);
        }
        else {
            insertAfter(p, closestPoint);
        }
        Log.d("TourM", "insertNearest end");
    }

    public void insertSmallest(Point p) {
        if (head == null) {
            insertBeginning(p);
        }
        else if (head.next == head) {
            insertNearest(p);
        }
        else {
            float origPathLength = totalDistance();
            float pathLengthAtBegin = origPathLength + distanceBetween(p, head.point);
            float pathLengthSmallest = pathLengthAtBegin;
            float pathLengthAtEnd = origPathLength + distanceBetween(p, head.prev.point);
            if (pathLengthAtEnd < pathLengthSmallest) {
                pathLengthSmallest = pathLengthAtEnd;
            }

            Log.d("TourM", "insertSmallest start");
            Node prevNode = head;
            Node nextNode = head.next;
            Node bestPrevNode = null;
            Node bestNextNode = null;
            while (nextNode != head) {
                Log.d("TourM", "insertSmallest eval");
                float pathLength = origPathLength - distanceBetween(prevNode.point, nextNode.point)
                        + distanceBetween(p, prevNode.point) + distanceBetween(p, nextNode.point);
                if (pathLength < pathLengthSmallest) {
                    bestPrevNode = prevNode;
                    bestNextNode = nextNode;
                    pathLengthSmallest = pathLength;
                }
                prevNode = nextNode;
                nextNode = nextNode.next;
            }

            if (pathLengthSmallest == pathLengthAtBegin) {
                insertBeginning(p);
            }
            else if (pathLengthSmallest == pathLengthAtEnd) {
                insertAfter(p, head.prev.point);
            }
            else {
                insertAfter(p, bestPrevNode.point);
            }
            Log.d("TourM", "insertSmallest end");
        }
    }

    public void reset() {
        head = null;
    }

    private class CircularLinkedListIterator implements Iterator<Point> {

        Node current;

        public CircularLinkedListIterator() {
            current = head;
        }

        @Override
        public boolean hasNext() {
            return (current != null);
        }

        @Override
        public Point next() {
            Point toReturn = current.point;
            current = current.next;
            if (current == head) {
                current = null;
            }
            return toReturn;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Iterator<Point> iterator() {
        return new CircularLinkedListIterator();
    }

}
