from py4j.java_gateway import java_import
from pymrgeo.vectormapop import VectorMapOp
from unittest import TestCase

class VectorMapOpTestSupport(TestCase):

    def __init__(self, mrgeo):
        self._mrgeo = mrgeo
        jvm = self._mrgeo._get_jvm()
        # Import the raster map op test support class and all other needed classes
        java_import(jvm, "org.mrgeo.mapalgebra.InlineCsvMapOp")
        self._jvm = jvm
        self._sparkContext = mrgeo.sparkContext

    def createVectorMapOpFromCsv(self, columns, data):
        mapop = self._jvm.InlineCsvMapOp.create(columns, data)
        return VectorMapOp(mapop=mapop, gateway=self._mrgeo.gateway, context=self._sparkContext)