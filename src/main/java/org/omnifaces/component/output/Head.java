/*
 * Copyright 2015 OmniFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.omnifaces.component.output;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.omnifaces.component.script.DeferredScript;

/**
 * Sample implementation of a page header component, capable of:
 * 
 * <ol>
 * <li>organizing script components after stylesheets</li>
 * <li>deferring script execution by delegation to the {@link DeferredScript} component</li>
 * </ol>
 * 
 * @author thazarik
 */
@FacesComponent(Head.COMPONENT_TYPE)
public class Head extends UIComponentBase {

    public static final String  COMPONENT_TYPE              = "org.omnifaces.component.output.Head";

    private static final String COMPONENT_FAMILY            = "javax.faces.Output";

    private static final String RENDERER_TYPE               = "javax.faces.Head";

    private static final String JAVAX_FACES_LOCATION_HEAD   = "javax_faces_location_HEAD";

    private static final String JAVAX_FACES_RESOURCE_SCRIPT = "javax.faces.resource.Script";

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public String getRendererType() {
        return RENDERER_TYPE;
    }

    @Override
    public void encodeAll(FacesContext context) throws IOException {
        UIViewRoot viewRoot = context.getViewRoot();
        List<UIComponent> componentResources = viewRoot.getFacet(JAVAX_FACES_LOCATION_HEAD).getChildren();
        if (!componentResources.isEmpty()) {
            List<UIComponent> scriptBlocks = new LinkedList<UIComponent>();

            // 1. remove script elements from component resources
            for (Iterator<UIComponent> iterator = componentResources.iterator(); iterator.hasNext();) {
                UIComponent resource = iterator.next();
                if (resource.getRendererType().contains(JAVAX_FACES_RESOURCE_SCRIPT)) {
                    scriptBlocks.add(resource);
                    iterator.remove();
                }
            }

            // 2. now defer and add them back the the end
            int pos = componentResources.size();
            for (UIComponent script : scriptBlocks) {
                componentResources.add(pos++, new DeferredScript(script));
            }
        }

        super.encodeAll(context);
    }

}