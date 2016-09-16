/*
 * Copyright 2009-2016 DigitalGlobe, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */

package org.mrgeo.mapalgebra

import org.mrgeo.mapalgebra.raster.RasterMapOp
import org.mrgeo.mapalgebra.utils.RasterMapOpTestVerifySupport
import org.mrgeo.utils.LatLng
import org.mrgeo.utils.tms.TMSUtils
import org.scalatest.{BeforeAndAfter, FlatSpec}

class CostDistanceMapOpTest extends FlatSpec with BeforeAndAfter with RasterMapOpTestVerifySupport {
//  behavior of "bounds calculation (with maxCost of METERS_PER_DEGREE seconds/meter and min friction value of 0.5"
//
//  val maxCost: Double = LatLng.METERS_PER_DEGREE
//  val minPixelValue: Double = 0.5
//  val EPSILON = 1e-5

  // With the maxCost value set to METERS_PER_DEGREE and the minPixelValue at 0.5, the bounds
  // should extend 2 degrees in all four directions beyond the MBR of the source points.

  // TODO:  Uncomment and correct when CostDistanceMapOp is finished
//  "Using a source point at lon=0, lat=0" should "return bounds from (-2, -2) to (2, 2)" in {
//    val sourcePoints: mutable.ListBuffer[(Float,Float)] = new mutable.ListBuffer[(Float,Float)]
//    sourcePoints.append((0.0f, 0.0f))
//    // The distance is 20000 meters for the expanded bounds.
//    val b: Bounds = CostDistanceMapOp.calculateBoundsFromCost(maxCost, sourcePoints, minPixelValue)
//    Assert.assertNotNull(b)
//    Assert.assertEquals(-2.0, b.getMinX, EPSILON)
//    Assert.assertEquals(-2.0, b.getMinY, EPSILON)
//    Assert.assertEquals(2.0, b.getMaxX, EPSILON)
//    Assert.assertEquals(2.0, b.getMaxY, EPSILON)
//  }
//
//  "Using a source point at lon=180, lat=90" should "return bounds from (178, 88) to (180, 90)" in {
//    val sourcePoints: mutable.ListBuffer[(Float,Float)] = new mutable.ListBuffer[(Float,Float)]
//    sourcePoints.append((180.0f, 90.0f))
//    // The distance is 20000 meters for the expanded bounds.
//    val b: Bounds = CostDistanceMapOp.calculateBoundsFromCost(maxCost, sourcePoints, minPixelValue)
//    Assert.assertNotNull(b)
//    Assert.assertEquals(178.0, b.getMinX, EPSILON)
//    Assert.assertEquals(88.0, b.getMinY, EPSILON)
//    Assert.assertEquals(180.0, b.getMaxX, EPSILON)
//    Assert.assertEquals(90.0, b.getMaxY, EPSILON)
//  }
//
//  "Using a source point at lon=180, lat=-90" should "return bounds from (178, -90) to (180, -88)" in {
//    val sourcePoints: mutable.ListBuffer[(Float,Float)] = new mutable.ListBuffer[(Float,Float)]
//    sourcePoints.append((180.0f, -90.0f))
//    // The distance is 20000 meters for the expanded bounds.
//    val b: Bounds = CostDistanceMapOp.calculateBoundsFromCost(maxCost, sourcePoints, minPixelValue)
//    Assert.assertNotNull(b)
//    Assert.assertEquals(178.0, b.getMinX, EPSILON)
//    Assert.assertEquals(-90.0, b.getMinY, EPSILON)
//    Assert.assertEquals(180.0, b.getMaxX, EPSILON)
//    Assert.assertEquals(-88.0, b.getMaxY, EPSILON)
//  }
//
//  "Using a source point at lon=-180, lat=-90" should "return bounds from (-180, -90) to (-178, -88)" in {
//    val sourcePoints: mutable.ListBuffer[(Float,Float)] = new mutable.ListBuffer[(Float,Float)]
//    sourcePoints.append((-180.0f, -90.0f))
//    // The distance is 20000 meters for the expanded bounds.
//    val b: Bounds = CostDistanceMapOp.calculateBoundsFromCost(maxCost, sourcePoints, minPixelValue)
//    Assert.assertNotNull(b)
//    Assert.assertEquals(-180.0, b.getMinX, EPSILON)
//    Assert.assertEquals(-90.0, b.getMinY, EPSILON)
//    Assert.assertEquals(-178.0, b.getMaxX, EPSILON)
//    Assert.assertEquals(-88.0, b.getMaxY, EPSILON)
//  }
//
//  "Using a source point at lon=-180, lat=90" should "return bounds from (-180, 88) to (-178, 90)" in {
//    val sourcePoints: mutable.ListBuffer[(Float,Float)] = new mutable.ListBuffer[(Float,Float)]
//    sourcePoints.append((-180.0f, 90.0f))
//    // The distance is 20000 meters for the expanded bounds.
//    val b: Bounds = CostDistanceMapOp.calculateBoundsFromCost(maxCost, sourcePoints, minPixelValue)
//    Assert.assertNotNull(b)
//    Assert.assertEquals(-180.0, b.getMinX, EPSILON)
//    Assert.assertEquals(88.0, b.getMinY, EPSILON)
//    Assert.assertEquals(-178.0, b.getMaxX, EPSILON)
//    Assert.assertEquals(90.0, b.getMaxY, EPSILON)
//  }
  behavior of "CostDistanceMapOp "

  private val maxCost = LatLng.METERS_PER_DEGREE
  private val tileSize = 512
  private val zoom = 3
  private val tileIds = Array[Long](20)
  private val srcPixelX = tileSize/2;
  private val srcPixelY = tileSize/2;
  private val srcPtLatLon = TMSUtils.pixelToLatLon(srcPixelX, srcPixelY, zoom, tileSize)
  private val sourcePoints = Array[Double](srcPtLatLon.lat, srcPtLatLon.lon)

  // Uniform cost value is resolution(deg/px) * meters/deg = meters/px
  private val uniformCostVal = TMSUtils.resolution(zoom, tileSize) * maxCost

  private var uniformFrictionMap: RasterMapOp = _;
  private var costMap: RasterMapOp = _
  private var subject: MapOp = _

  before {
    uniformFrictionMap = createUniformFrictionMap
  }

  after {
    stopSparkContext
  }


  "Using a uniform friction surface with a value of the number of meters per pixel and a maximum cost of meters per " +
  "degree with src points in a double arrary" should "create an output map with the value of each pixel being the " +
  "distance in meters from the center of that pixel to the source point out to a bounds of the number of pixels in a " +
  "degree" in {
    subject = CostDistanceMapOp.create(uniformFrictionMap, maxCost, zoom, sourcePoints)
    subject.execute(uniformFrictionMap.context());
    val costMapRdd = subject.asInstanceOf[RasterMapOp].rdd.getOrElse(fail("No RDD returned from map operation"))
    val (srcLat, srcLon) = (srcPtLatLon.lat, srcPtLatLon.lon)
    verifyRastersNoData(costMapRdd, tileIds, tileSize, zoom, Array[Double](Float.NaN.asInstanceOf[Double]),
                        srcLon - 1.0, srcLon + 1.0, srcLat + 1, srcLat - 1,
                        Some((b: Int, x: Int, y: Int, sample: Double) => {
                          // Verify that the sample is equal to the distance from the center of pixel at x, y to the
                          // src point in meters
                          assertResult(math.sqrt(math.pow(x - srcPixelX, 2) + math.pow(y - srcPixelY, 2))) {
                            sample
                          }
                        }))
  }

  "Using a uniform friction surface with a value of the number of meters per pixel and a maximum cost of meters per " +
  "degree with src points in vector map op" should "create an output map with the value of each pixel being the " +
  "distance in meters from the center of that pixel to the source point out to a bounds of the number of pixels in a " +
  "degree" in {
    val (srcLat, srcLon) = (srcPtLatLon.lat, srcPtLatLon.lon)
    val srcPointsVector = new  InlineCsvMapOp("GEOMETRY", s"POINT(${srcLat} ${srcLon})")
    // Populate the vector RDD with the csv data
    srcPointsVector.execute(uniformFrictionMap.context())
    subject = CostDistanceMapOp.create(uniformFrictionMap, maxCost, zoom, srcPointsVector)
    subject.execute(uniformFrictionMap.context());
    val costMapRdd = subject.asInstanceOf[RasterMapOp].rdd.getOrElse(fail("No RDD returned from map operation"))
    verifyRastersNoData(costMapRdd, tileIds, tileSize, zoom, Array[Double](Float.NaN.asInstanceOf[Double]),
      srcLon - 1.0, srcLon + 1.0, srcLat + 1, srcLat - 1,
      Some((b: Int, x: Int, y: Int, sample: Double) => {
        // Verify that the sample is equal to the distance from the center of pixel at x, y to the
        // src point in meters
        assertResult(math.sqrt(math.pow(x - srcPixelX, 2) + math.pow(y - srcPixelY, 2))) {
          sample
        }
      }))
  }

  // TODO - Probably should add a test for a directional friction surface.  Suggestion would be to have each band have
  // a uniform friction surface equal to the number of meters per pixel * the band number + 1.  Verifying the cost out
  // to more than 1 pixel beyond the src, would get tricky, so recommend either limiting the checks to the immediate
  // adjacent pixels, or to only the n, ne, e, se, s, sw, w, nw directions

  def createUniformFrictionMap = createRasterMapOp(tileIds, zoom, tileSize, imageInitialData = Some(Array[Double](uniformCostVal)))
}
