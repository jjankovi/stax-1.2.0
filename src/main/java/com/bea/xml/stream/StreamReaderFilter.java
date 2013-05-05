/*   Copyright 2004 BEA Systems, Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.bea.xml.stream;

import javax.xml.namespace.QName;

import org.xml.stream.StreamFilter;
import org.xml.stream.XMLInputFactory;
import org.xml.stream.XMLStreamException;
import org.xml.stream.XMLStreamReader;
import org.xml.stream.events.XMLEvent;

import com.bea.xml.stream.filters.NameFilter;
import com.bea.xml.stream.filters.TypeFilter;

/**
 * <p> Apply a filter to the StreamReader </p>
 */

public class StreamReaderFilter 
  extends ReaderDelegate
{
  private StreamFilter filter;

  public StreamReaderFilter(XMLStreamReader reader) {
    super(reader);
  }

  public StreamReaderFilter(XMLStreamReader reader,
                            StreamFilter filter) {
    super(reader);
    setFilter(filter);
  }

  public void setFilter(StreamFilter filter) {
    this.filter = filter;
  }

  public int next()
    throws XMLStreamException
  {
    if (hasNext())
      return super.next();
    throw new IllegalStateException("next() may not be called "+
                                    " when there are no more "+
                                    " items to return");
  }

  public boolean hasNext()
    throws XMLStreamException
  {
    while (super.hasNext()) {
      if (filter.accept(getDelegate())) return true;
      super.next();
    }
    return false;
  }


  public static void main(String args[]) throws Exception {
    System.setProperty("javax.xml.stream.XMLInputFactory", 
                       "com.bea.xml.stream.MXParserFactory");

    XMLInputFactory factory = XMLInputFactory.newInstance();

    TypeFilter f = new com.bea.xml.stream.filters.TypeFilter();
    f.addType(XMLEvent.START_ELEMENT);
    f.addType(XMLEvent.END_ELEMENT);
    XMLStreamReader reader = factory.createFilteredReader(
      factory.createXMLStreamReader(new java.io.FileReader(args[0])),(StreamFilter)f);
    while(reader.hasNext()) {
      System.out.println(reader.getLocalName());
      reader.next();
    }

    
    NameFilter nf = new NameFilter(new QName("banana","B"));
    XMLStreamReader reader2 = factory.createFilteredReader(
      factory.createXMLStreamReader(new java.io.FileReader(args[0])),(StreamFilter)nf);
    
    XMLStreamRecorder r = new XMLStreamRecorder(new java.io.OutputStreamWriter(new java.io.FileOutputStream("out.stream")));


    while(reader2.hasNext()) {
      r.write(reader2);
      reader2.next();
    }
    r.flush();
    
  }
}
