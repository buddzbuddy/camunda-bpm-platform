/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.engine.impl.optimize;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricDecisionInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricVariableUpdate;
import org.camunda.bpm.engine.impl.HistoricDecisionInstanceQueryImpl;
import org.camunda.bpm.engine.impl.persistence.AbstractManager;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.camunda.bpm.engine.authorization.Permissions.READ_HISTORY;
import static org.camunda.bpm.engine.authorization.Resources.DECISION_DEFINITION;
import static org.camunda.bpm.engine.authorization.Resources.PROCESS_DEFINITION;

public class OptimizeManager extends AbstractManager {

  @SuppressWarnings("unchecked")
  public List<HistoricActivityInstance> getCompletedHistoricActivityInstances(Date finishedAfter,
                                                                              Date finishedAt,
                                                                              int maxResults) {
    checkIsAuthorizedToReadHistoryOfProcessDefinitions();

    Map<String, Object> params = new HashMap<String, Object>();
    params.put("finishedAfter", finishedAfter);
    params.put("finishedAt", finishedAt);
    params.put("maxResults", maxResults);

    return getDbEntityManager().selectList("selectCompletedHistoricActivityPage", params);
  }

  @SuppressWarnings("unchecked")
  public List<HistoricActivityInstance> getRunningHistoricActivityInstances(Date startedAfter,
                                                                            Date startedAt,
                                                                            int maxResults) {
    checkIsAuthorizedToReadHistoryOfProcessDefinitions();

    Map<String, Object> params = new HashMap<String, Object>();
    params.put("startedAfter", startedAfter);
    params.put("startedAt", startedAt);
    params.put("maxResults", maxResults);

    return getDbEntityManager().selectList("selectRunningHistoricActivityPage", params);
  }

  private void checkIsAuthorizedToReadHistoryOfProcessDefinitions() {
    getAuthorizationManager().checkAuthorization(READ_HISTORY, PROCESS_DEFINITION);
  }

  @SuppressWarnings("unchecked")
  public List<HistoricProcessInstance> getCompletedHistoricProcessInstances(Date finishedAfter,
                                                                            Date finishedAt,
                                                                            int maxResults) {
    checkIsAuthorizedToReadHistoryOfProcessDefinitions();

    Map<String, Object> params = new HashMap<String, Object>();
    params.put("finishedAfter", finishedAfter);
    params.put("finishedAt", finishedAt);
    params.put("maxResults", maxResults);

    return getDbEntityManager().selectList("selectCompletedHistoricProcessInstancePage", params);
  }

  @SuppressWarnings("unchecked")
  public List<HistoricProcessInstance> getRunningHistoricProcessInstances(Date startedAfter,
                                                                          Date startedAt,
                                                                          int maxResults) {
    checkIsAuthorizedToReadHistoryOfProcessDefinitions();

    Map<String, Object> params = new HashMap<String, Object>();
    params.put("startedAfter", startedAfter);
    params.put("startedAt", startedAt);
    params.put("maxResults", maxResults);

    return getDbEntityManager().selectList("selectRunningHistoricProcessInstancePage", params);
  }

  @SuppressWarnings("unchecked")
  public List<HistoricVariableUpdate> getHistoricVariableUpdates(Date occurredAfter,
                                                                 Date occurredAt,
                                                                 int maxResults) {
    checkIsAuthorizedToReadHistoryOfProcessDefinitions();

    Map<String, Object> params = new HashMap<String, Object>();
    params.put("occurredAfter", occurredAfter);
    params.put("occurredAt", occurredAt);
    params.put("maxResults", maxResults);

    return getDbEntityManager().selectList("selectHistoricVariableUpdatePage", params);
  }

  @SuppressWarnings("unchecked")
  public List<HistoricDecisionInstance> getHistoricDecisionInstances(Date evaluatedAfter,
                                                                     Date evaluatedAt,
                                                                     int maxResults) {
    checkIsAuthorizedToReadHistoryOfDecisionDefinitions();

    Map<String, Object> params = new HashMap<String, Object>();
    params.put("evaluatedAfter", evaluatedAfter);
    params.put("evaluatedAt", evaluatedAt);
    params.put("maxResults", maxResults);

    List<HistoricDecisionInstance> decisionInstances =
      getDbEntityManager().selectList("selectHistoricDecisionInstancePage", params);

    HistoricDecisionInstanceQueryImpl query =
      (HistoricDecisionInstanceQueryImpl) new HistoricDecisionInstanceQueryImpl()
        .disableBinaryFetching()
        .disableCustomObjectDeserialization()
        .includeInputs()
        .includeOutputs();

    getHistoricDecisionInstanceManager()
      .enrichHistoricDecisionsWithInputsAndOutputs(query, decisionInstances);

    return decisionInstances;
  }

  private void checkIsAuthorizedToReadHistoryOfDecisionDefinitions() {
    getAuthorizationManager().checkAuthorization(READ_HISTORY, DECISION_DEFINITION);
  }

}
