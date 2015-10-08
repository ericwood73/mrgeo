package org.mrgeo.mapalgebra.raster

import java.io.{IOException, ObjectInput, ObjectOutput, Externalizable}

import org.apache.spark.{SparkConf, SparkContext}
import org.mrgeo.data.ProviderProperties
import org.mrgeo.data.rdd.RasterRDD
import org.mrgeo.image.MrsImagePyramidMetadata
import org.mrgeo.mapalgebra.{MapAlgebra, MapOp}
import org.mrgeo.mapalgebra.parser.{ParserException, ParserNode}
import org.mrgeo.job.JobArguments


class SaveRasterMapOp extends RasterMapOp with Externalizable {

  private var rasterRDD: Option[RasterRDD] = None
  private var input: Option[RasterMapOp] = None
  private var output:String = null

  private[mapalgebra] def this(node: ParserNode, variables: String => Option[ParserNode]) = {
    this()

    if (node.getNumChildren != 2) {
      throw new ParserException(node.getName + " takes 2 arguments")
    }

    input = RasterMapOp.decodeToRaster(node.getChild(0), variables)
    output = MapOp.decodeString(node.getChild(1)) match {
    case Some(s) => s
    case _ => throw new ParserException("Error decoding String")
    }

  }

  override def rdd(): Option[RasterRDD] = rasterRDD

  override def execute(context: SparkContext): Boolean = {
    input match {
    case Some(pyramid) =>

      rasterRDD = pyramid.rdd()
      val meta = new MrsImagePyramidMetadata(pyramid.metadata() getOrElse (throw new IOException("Can't load metadata! Ouch! " + pyramid.getClass.getName)))

      // set the pyramid name to the output
      meta.setPyramid(output)
      metadata(meta)

      pyramid.save(output, providerProperties, context)

    case None => throw new IOException("Error saving raster")
    }

    true
  }

  var providerProperties:ProviderProperties = null

  override def setup(job: JobArguments, conf:SparkConf): Boolean = {
    providerProperties = ProviderProperties.fromDelimitedString(job.getSetting(MapAlgebra.ProviderProperties, ""))
    true
  }

  override def teardown(job: JobArguments, conf:SparkConf): Boolean = true
  override def readExternal(in: ObjectInput): Unit = {}
  override def writeExternal(out: ObjectOutput): Unit = {}

}