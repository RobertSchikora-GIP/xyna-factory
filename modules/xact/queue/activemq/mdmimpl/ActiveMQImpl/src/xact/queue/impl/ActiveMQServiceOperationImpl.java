/*
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
 * Copyright 2022 Xyna GmbH, Germany
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
 */
package xact.queue.impl;

import xact.queue.ActiveMQ;
import xact.queue.QueueName;
import xact.queue.admin.ActiveMQConfig;

import com.gip.xyna.XynaFactory;
import com.gip.xyna.utils.exceptions.XynaException;
import com.gip.xyna.xdev.xfractmod.xmdm.XynaObject.BehaviorAfterOnUnDeploymentTimeout;
import com.gip.xyna.xdev.xfractmod.xmdm.XynaObject.ExtendedDeploymentTask;
import com.gip.xyna.xfmg.xfctrl.queuemgmnt.ActiveMQConnectData;
import com.gip.xyna.xfmg.xfctrl.queuemgmnt.Queue;
import com.gip.xyna.xfmg.xfctrl.queuemgmnt.QueueConnectData;
import com.gip.xyna.xfmg.xfctrl.queuemgmnt.QueueManagement;
import com.gip.xyna.xfmg.xfctrl.queuemgmnt.QueueManagement.QueueInstanceBuilder;
import com.gip.xyna.xfmg.xfctrl.queuemgmnt.QueueType;


public class ActiveMQServiceOperationImpl implements ExtendedDeploymentTask {

  private static long revision;
  private static final QueueType queueType = QueueType.ACTIVE_MQ;

  public void onDeployment() throws XynaException {
    // do something on deployment, if required
    // This is executed again on each classloader-reload, that is each
    // time a dependent object is redeployed, for example a type of an input parameter.
    revision = XynaFactory.getInstance().getFactoryManagement().getXynaFactoryControl().getRevisionManagement().getRevision(getClass());
    getQueueManagement().registerQueueInstanceBuilder( queueType, revision, new ActiveMQBuilder() );
  }

  public void onUndeployment() throws XynaException {
    // do something on undeployment, if required
    // This is executed again on each classloader-unload, that is each
    // time a dependent object is redeployed, for example a type of an input parameter.
    getQueueManagement().unregisterQueueInstanceBuilder(queueType, revision);
  }

  public Long getOnUnDeploymentTimeout() {
    // The (un)deployment runs in its own thread. The service may define a timeout
    // in milliseconds, after which Thread.interrupt is called on this thread.
    // If null is returned, the default timeout (defined by XynaProperty xyna.xdev.xfractmod.xmdm.deploymenthandler.timeout) will be used.;
    return null;
  }

  public BehaviorAfterOnUnDeploymentTimeout getBehaviorAfterOnUnDeploymentTimeout() {
    // Defines the behavior of the (un)deployment after reaching the timeout and if this service ignores a Thread.interrupt.
    // - BehaviorAfterOnUnDeploymentTimeout.EXCEPTION: Deployment will be aborted, while undeployment will log the exception and NOT abort.
    // - BehaviorAfterOnUnDeploymentTimeout.IGNORE: (Un)Deployment will be continued in another thread asynchronously.
    // - BehaviorAfterOnUnDeploymentTimeout.KILLTHREAD: (Un)Deployment will be continued after calling Thread.stop on the thread.
    //   executing the (Un)Deployment.
    // If null is returned, the factory default <IGNORE> will be used.
    return null;
  }

  private QueueManagement getQueueManagement() {
    return XynaFactory.getInstance().getFactoryManagement().getXynaFactoryControl().getQueueManagement();
  }

  public static class ActiveMQBuilder implements QueueInstanceBuilder {

    @Override
    public Object build(Queue queue) {
      ActiveMQConfig cfg = new ActiveMQConfig();
      cfg.setName_externalQueue(queue.getExternalName());
      cfg.setName_unique(new QueueName(queue.getUniqueName()));
      QueueConnectData connData = queue.getConnectData();
      if (connData instanceof ActiveMQConnectData) {
        ActiveMQConnectData amcd = (ActiveMQConnectData) connData;
        cfg.setHostname(amcd.getHostname());
        cfg.setPort(amcd.getPort());
      } else {
        throw new IllegalStateException("Expected ActiveMQConnectData, got " + connData);
      }
      return new ActiveMQ(cfg);
    }

  }

}
