from py4j.java_gateway import java_import
from unittest import TestSuite, TestCase, defaultTestLoader, main
from pymrgeo.mrgeo import MrGeo
from rastermapoptestsupport import RasterMapOpTestSupport
from vectormapoptestsupport import VectorMapOpTestSupport

class CostDistanceTests(TestCase):

    _mrgeo = None

    _tileSize = 512
    _zoom = 3
    _srcPixelX = _tileSize/2;
    _srcPixelY = _tileSize/2;

    @classmethod
    def setUpClass(cls):
        print("*** CostDistanceTests.setUpClass()")
        cls._mrgeo = MrGeo()

    @classmethod
    def tearDownClass(cls):
        print("*** CostDistanceTests.tearDownClass()")
        cls._mrgeo.disconnect()

    def setUp(self):
        mrgeo = self._mrgeo

        # Get the JVM.  This will create the gateway
        self._jvm = mrgeo._get_jvm()
        mrgeo.usedebug()
        mrgeo.start()

        java_import(self._jvm, "org.mrgeo.utils.tms.*")
        self._maxCost = self._jvm.LatLng.METERS_PER_DEGREE
        self._srcPtLatLon = self._jvm.TMSUtils.pixelToLatLon(self._srcPixelX, self._srcPixelY, self._zoom, self._tileSize)
        self._uniformCostVal = self._jvm.TMSUtils.resolution(self._zoom, self._tileSize) * self._maxCost

        tileIds = self._getArray(self._jvm.long, 1)
        tileIds[0] = 20
        self._tileIds = tileIds

        self._sparkContext = mrgeo.sparkContext

        self._mrgeo = mrgeo
        self._rasterMapOpTestSupport = RasterMapOpTestSupport(self._mrgeo)
        self._vectorMapOpTestSupport = VectorMapOpTestSupport(self._mrgeo)
        self.createUniformFrictionSurface()
        self.createSubjectMapOp()

    def createUniformFrictionSurface(self):
        imageInitialData = self._getArray(self._jvm.double, 1)
        imageInitialData[0] = self._uniformCostVal
        self._uniformFrictionSurface = \
                self._rasterMapOpTestSupport.createRasterMapOp(self._tileIds, self._zoom, self._tileSize,
                                                               imageInitialData = imageInitialData)

    # The mapop upon which the costDistance is invoked is not used for the cost distance calculation or result, so
    # we can just create an empty mapop with defaults to give us an object to invoke costDistance on
    def createSubjectMapOp(self):
        self._subject = self._rasterMapOpTestSupport.createRasterMapOp(self._tileIds, self._zoom, self._tileSize)

    def test_cost_distance_using_points(self):
        sourcePoints = self._getArray(self._jvm.double, 2)
        sourcePoints[0] = self._srcPtLatLon.lat
        sourcePoints[1] = self._srcPtLatLon.lon
        result = self._subject.costDistance(self._uniformFrictionSurface, self._maxCost, self._zoom, sourcePoints)
        # TODO verify the result.  See org.mrgeo.mapalgebra.CostDistanceMapOpTest for an example

    def test_cost_distance_using_vector(self):
        (srcLat, srcLon) = (self._srcPtLatLon.lat, self._srcPtLatLon.lon)
        srcPointsVector = self._vectorMapOpTestSupport("GEOMETRY", "POINT({0} {1})".format(srcLat, srcLon))
        result = self._subject.costDistance(self._uniformFrictionSurface, self._maxCost, self._zoom, srcPointsVector)
        # TODO verify the result.  See org.mrgeo.mapalgebra.CostDistanceMapOpTest for an example