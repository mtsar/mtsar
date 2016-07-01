/*
 * Copyright 2015 Dmitry Ustalov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mtsar.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.coding.*;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.distance.NominalDistanceFunction;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.distance.OrdinalDistanceFunction;
import mtsar.api.sql.AnswerDAO;
import org.inferred.freebuilder.FreeBuilder;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Experimental wrapper for various agreement tests.
 */
@FreeBuilder
@XmlRootElement
@JsonDeserialize(builder = AgreementReport.Builder.class)
public interface AgreementReport {
    @JsonProperty
    double getPercentage();

    @JsonProperty
    double getWeightedKappa();

    @JsonProperty
    double getNominalAlpha();

    @JsonProperty
    double getOrdinalAlpha();

    @JsonProperty
    double getRandolphKappa();

    @JsonPOJOBuilder(withPrefix = "set")
    class Builder extends AgreementReport_Builder {
        public Builder compute(Stage stage, AnswerDAO answerDAO) {
            final List<Answer> answers = answerDAO.listForStage(stage.getId());

            final Map<Integer, List<Answer>> answerTasks = answers.stream().
                    filter(answer -> answer.getType().equalsIgnoreCase(AnswerDAO.ANSWER_TYPE_ANSWER)).
                    collect(Collectors.groupingBy(Answer::getTaskId));

            final AtomicInteger workersCount = new AtomicInteger();
            final Map<Integer, Integer> workers = answers.stream().
                    filter(answer -> answer.getType().equalsIgnoreCase(AnswerDAO.ANSWER_TYPE_ANSWER)).
                    map(Answer::getWorkerId).distinct().
                    collect(Collectors.toMap(Function.identity(), workerId -> workersCount.getAndIncrement()));

            final CodingAnnotationStudy study = new CodingAnnotationStudy(workers.size());
            for (final List<Answer> taskAnswers : answerTasks.values()) {
                final String items[] = new String[workers.size()];
                for (final Answer answer : taskAnswers) {
                    items[workers.get(answer.getWorkerId())] = answer.getAnswer().get();
                }
                study.addItemAsArray(items);
            }

            final PercentageAgreement percent = new PercentageAgreement(study);
            super.setPercentage(percent.calculateAgreement());

            final KrippendorffAlphaAgreement alphaNominal = new KrippendorffAlphaAgreement(study, new NominalDistanceFunction());
            super.setNominalAlpha(alphaNominal.calculateAgreement());

            final KrippendorffAlphaAgreement alphaOrdinal = new KrippendorffAlphaAgreement(study, new OrdinalDistanceFunction());
            super.setOrdinalAlpha(alphaOrdinal.calculateAgreement());

            final WeightedKappaAgreement weightedKappa = new WeightedKappaAgreement(study, new NominalDistanceFunction());
            super.setWeightedKappa(weightedKappa.calculateAgreement());

            final RandolphKappaAgreement randolphKappa = new RandolphKappaAgreement(study);
            super.setRandolphKappa(randolphKappa.calculateAgreement());

            return this;
        }
    }
}
