/*
 * Copyright 2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package africa.absa.inception.mail;

import freemarker.cache.TemplateLoader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * The <b>FreeMarkerTemplateLoader</b> class implements the Apache FreeMarker template loader.
 *
 * @author Marcus Portmann
 */
public class FreeMarkerTemplateLoader implements TemplateLoader {

  /** The Mail Service. */
  private final IMailService mailService;

  /**
   * Constructs a new <b>FreeMarkerTemplateLoader</b>.
   *
   * @param mailService the Mail Service
   */
  public FreeMarkerTemplateLoader(IMailService mailService) {
    this.mailService = mailService;
  }

  /**
   * Closes the template source, releasing any resources held that are only required for reading the
   * template and/or its metadata. This is the last method that is called by the {@link
   * freemarker.cache.TemplateCache} for a template source, except that {@link
   * Object#equals(Object)} is might called later too. {@link freemarker.cache.TemplateCache}
   * ensures that this method will be called on every object that is returned from {@link
   * #findTemplateSource(String)}.
   *
   * @param templateSource the template source that should be closed.
   */
  @Override
  public void closeTemplateSource(Object templateSource) throws IOException {}

  /**
   * Finds the template in the backing storage and returns an object that identifies the storage
   * location where the template can be loaded from. See the return value for more information.
   *
   * @param name The name of the template, already localized and normalized by the {@link
   *     freemarker.cache.TemplateCache cache}.
   * @return An object representing the template source, which can be supplied in subsequent calls
   *     to {@link #getLastModified(Object)} and {@link #getReader(Object, String)}, when those are
   *     called on the same {@link TemplateLoader}. {@code null} must be returned if the source for
   *     the template doesn't exist
   */
  @Override
  public Object findTemplateSource(String name) throws IOException {
    try {
      MailTemplate mailTemplate = mailService.getMailTemplate(name);

      return mailTemplate.getId();
    } catch (MailTemplateNotFoundException e) {
      return null;
    } catch (Throwable e) {
      throw new IOException("Failed to find the template source (" + name + ")", e);
    }
  }

  /**
   * Returns the time of last modification of the specified template source. This method is called
   * after <b>findTemplateSource()</b>.
   *
   * @param templateSource an object representing a template source, obtained through a prior call
   *     to {@link #findTemplateSource(String)}
   * @return the time of last modification of the specified template source, or -1 if the time is
   *     not known.
   */
  @Override
  public long getLastModified(Object templateSource) {
    try {
      if (templateSource instanceof String) {
        MailTemplate mailTemplate = mailService.getMailTemplate((String) templateSource);

        LocalDateTime lastModified = mailTemplate.getLastModified();

        if (lastModified != null) {
          return lastModified.toInstant(ZoneOffset.UTC).toEpochMilli();
        } else {
          return -1;
        }
      }
    } catch (Throwable ignored) {
    }

    return -1;
  }

  /**
   * Returns the character stream of a template represented by the specified template source. This
   * method is possibly called for multiple times for the same template source object, and it must
   * always return a {@link Reader} that reads the template from its beginning. Before this method
   * is called for the second time (or later), its caller must close the previously returned {@link
   * Reader}, and it must not use it anymore. That is, this method is not required to support
   * multiple concurrent readers for the same source {@code templateSource} object.
   *
   * <p>Typically, this method is called if the template is missing from the cache, or if after
   * calling {@link #findTemplateSource(String)} and {@link #getLastModified(Object)} it was
   * determined that the cached copy of the template is stale. Then, if it turns out that the {@code
   * encoding} parameter used doesn't match the actual template content (based on the {@code #ftl
   * encoding=...} header), this method will be called for a second time with the correct {@code
   * encoding} parameter value.
   *
   * @param templateSource an object representing a template source, obtained through a prior call
   *     to {@link #findTemplateSource(String)}. This must be an object on which {@link
   *     TemplateLoader#closeTemplateSource(Object)} wasn't applied yet.
   * @param encoding the character encoding used to translate source bytes to characters. Some
   *     loaders may not have access to the byte representation of the template stream, and instead
   *     directly obtain a character stream. These loaders should ignore the encoding parameter.
   * @return A {@link Reader} representing the template character stream. It's the responsibility of
   *     the caller (which is {@link freemarker.cache.TemplateCache} usually) to {@code close()} it.
   *     The {@link Reader} is not required to work after the {@code templateSource} was closed
   *     ({@link #closeTemplateSource(Object)}).
   */
  @Override
  public Reader getReader(Object templateSource, String encoding) throws IOException {
    try {
      if (templateSource instanceof String) {
        MailTemplate mailTemplate = mailService.getMailTemplate((String) templateSource);

        return new StringReader(new String(mailTemplate.getTemplate(), StandardCharsets.UTF_8));
      } else {
        throw new RuntimeException("Invalid template source (" + templateSource + ")");
      }
    } catch (Throwable e) {
      throw new IOException(
          "Failed to retrieve the character stream for the template (" + templateSource + ")", e);
    }
  }
}
