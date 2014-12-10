/*
 * Copyright 2009-2014 DigitalGlobe, Inc.
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
 */
package org.mrgeo.geometryfilter;

import org.mrgeo.geometry.WritableGeometry;

public class AreaGeometryFilter extends GeometryFilter
{
  private static final long serialVersionUID = 1L;
  String attributeName = "area";
  
  public AreaGeometryFilter()
  {
  }
  
  public AreaGeometryFilter(String attributeName)
  {
    this.attributeName = attributeName;
  }
  
  @Override
  public WritableGeometry filterInPlace(WritableGeometry g)
  {
    double area = JtsConverter.convertToJts(g).getArea();
    g.setAttribute(attributeName, Double.toString(area));
    return g;
  }
}
