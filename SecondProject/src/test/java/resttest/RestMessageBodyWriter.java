/**
 * 
 */
package resttest;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.oracle.sites.visitors.api.visitor.LinkedProfilesImpl;
import com.oracle.sites.visitors.core.storage.visitor.beans.ExtendedAttributeImpl;
import com.oracle.sites.visitors.core.storage.visitor.beans.StoredProfileImpl;

/**
 * @author napattan
 *
 */
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class RestMessageBodyWriter<T> implements MessageBodyWriter<T> {

	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return Boolean.TRUE;
	}

	@Override
	public long getSize(T restBean, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return 0;
	}

	@Override
	public void writeTo(T restBean, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException,
			WebApplicationException {

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(type);

			/** serialize the entity restBean to the entity output stream */
			jaxbContext.createMarshaller().marshal(restBean, entityStream);
		} catch (JAXBException jaxbException) {
			throw new ProcessingException(
					"Error serializing a restBean to the output stream",
					jaxbException);

		}
	}

}