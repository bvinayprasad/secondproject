/**
 * 
 */
package resttest;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * @author napattan
 * @param <T>
 *
 */
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class RestMessageBodyReader<T> implements MessageBodyReader<T> {

	@Override
	public boolean isReadable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return Boolean.TRUE;
	}

	@Override
	public T readFrom(Class<T> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(type);
			Object restBean = jaxbContext.createUnmarshaller().unmarshal(
					entityStream);
			return type.cast(restBean);
		} catch (JAXBException jaxbException) {
			throw new ProcessingException("Error deserializing a RestBean.",
					jaxbException);
		}
	}
}
