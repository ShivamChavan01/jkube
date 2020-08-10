/**
 * Copyright (c) 2019 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at:
 *
 *     https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.jkube.maven.plugin.mojo.develop;

import java.io.File;
import java.util.List;

import org.eclipse.jkube.kit.config.resource.RuntimeMode;
import org.eclipse.jkube.maven.plugin.mojo.OpenShift;

import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import static org.eclipse.jkube.maven.plugin.mojo.build.ApplyMojo.DEFAULT_OPENSHIFT_MANIFEST;

/**
 * Undeploys (deletes) the kubernetes resources generated by the current project.
 * <br>
 * This goal is the opposite to the <code>oc:run</code> or <code>oc:deploy</code> goals.
 */
@Mojo(name = "undeploy", requiresDependencyResolution = ResolutionScope.COMPILE, defaultPhase = LifecyclePhase.INSTALL)
public class OpenshiftUndeployMojo extends UndeployMojo {

    /**
     * The S2I binary builder BuildConfig name suffix appended to the image name to avoid
     * clashing with the underlying BuildConfig for the Jenkins pipeline
     */
    @Parameter(property = "jkube.s2i.buildNameSuffix", defaultValue = "-s2i")
    protected String s2iBuildNameSuffix;

    /**
     * The generated openshift YAML file
     */
    @Parameter(property = "jkube.openshiftManifest", defaultValue = DEFAULT_OPENSHIFT_MANIFEST)
    protected File openshiftManifest;

    @Parameter(property = "jkube.openshiftImageStreamManifest", defaultValue = "${basedir}/target/${project.artifactId}-is.yml")
    protected File openshiftImageStreamManifest;

    @Override
    protected List<File> getManifestsToUndeploy() {
        final List<File> ret = super.getManifestsToUndeploy();
        ret.add(openshiftImageStreamManifest);
        return ret;
    }

    @Override
    public File getManifest(KubernetesClient kubernetesClient) {
        return OpenShift.getOpenShiftManifest(kubernetesClient, getKubernetesManifest(), openshiftManifest);
    }

    @Override
    protected RuntimeMode getRuntimeMode() {
        return RuntimeMode.OPENSHIFT;
    }

    @Override
    protected String getLogPrefix() {
        return OpenShift.DEFAULT_LOG_PREFIX;
    }

}
