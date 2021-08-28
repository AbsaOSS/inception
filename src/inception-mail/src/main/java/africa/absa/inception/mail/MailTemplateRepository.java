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

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * The <b>MailTemplateRepository</b> interface declares the repository for the <b> MailTemplate</b>
 * domain type.
 *
 * @author Marcus Portmann
 */
public interface MailTemplateRepository extends JpaRepository<MailTemplate, String> {

  @Modifying
  @Query("delete from MailTemplate mt where mt.id = :mailTemplateId")
  void deleteById(@Param("mailTemplateId") String mailTemplateId);

  @Query("select mt.name from MailTemplate mt where mt.id = :mailTemplateId")
  Optional<String> getNameById(@Param("mailTemplateId") String mailTemplateId);

  @Query("select mt.updated from MailTemplate mt where mt.id = :mailTemplateId")
  Optional<LocalDateTime> getUpdatedById(@Param("mailTemplateId") String mailTemplateId);
}
