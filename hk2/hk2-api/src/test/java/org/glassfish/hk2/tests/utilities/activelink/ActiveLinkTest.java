/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.hk2.tests.utilities.activelink;

import java.util.Set;

import javax.inject.Singleton;

import junit.framework.Assert;

import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class ActiveLinkTest {
    private final static String NAME = "name";
    
    /**
     * Tests a simple and mostly empty descriptor
     */
    @Test
    public void testOnlyImpl() {
        AbstractActiveDescriptor<?> desc = BuilderHelper.activeLink(ServiceA.class).build();
        
        Assert.assertSame(ServiceA.class, desc.getImplementationClass());
        Assert.assertSame(ServiceA.class.getName(), desc.getImplementation());
        
        Assert.assertNull(desc.getName());
        
        Assert.assertEquals(PerLookup.class, desc.getScopeAnnotation());
        Assert.assertEquals(PerLookup.class.getName(), desc.getScope());
        
        Assert.assertTrue(desc.getAdvertisedContracts().isEmpty());
        Assert.assertTrue(desc.getContractTypes().isEmpty());
        
        Assert.assertTrue(desc.getQualifiers().isEmpty());
        Assert.assertTrue(desc.getQualifierAnnotations().isEmpty());
        
        Assert.assertNull(desc.getBaseDescriptor());
        Assert.assertNull(desc.getLoader());
        Assert.assertSame(DescriptorType.CLASS, desc.getDescriptorType());
        Assert.assertTrue(desc.getInjectees().isEmpty());
        
        Assert.assertFalse(desc.isReified());
    }
    
    @Test
    public void testDescWithFields() {
        SimpleQualifier1 sq1 = new SimpleQualifier1Impl();
        
        AbstractActiveDescriptor<?> desc = BuilderHelper.activeLink(ServiceA.class).
                to(SimpleInterface1.class).
                in(Singleton.class).
                qualifiedBy(sq1).
                named(NAME).
                has(NAME, NAME).
                ofRank(1).
                andLoadWith(new HK2Loader() {

                    @Override
                    public Class<?> loadClass(String className)
                            throws MultiException {
                        throw new AssertionError("not called");
                    }
                    
                }).
                build();
                
        
        Assert.assertSame(ServiceA.class, desc.getImplementationClass());
        Assert.assertSame(ServiceA.class.getName(), desc.getImplementation());
        
        Assert.assertSame(NAME, desc.getName());
        
        Assert.assertEquals(Singleton.class, desc.getScopeAnnotation());
        Assert.assertEquals(Singleton.class.getName(), desc.getScope());
        
        testSetOfOne(desc.getAdvertisedContracts(), SimpleInterface1.class.getName());
        testSetOfOne(desc.getContractTypes(), SimpleInterface1.class);
        
        testSetOfOne(desc.getQualifierAnnotations(), sq1);
        testSetOfOne(desc.getQualifiers(), SimpleQualifier1.class.getName());
        
        Assert.assertNull(desc.getBaseDescriptor());
        Assert.assertNotNull(desc.getLoader());
        Assert.assertSame(DescriptorType.CLASS, desc.getDescriptorType());
        Assert.assertTrue(desc.getInjectees().isEmpty());
        
        Assert.assertFalse(desc.isReified());
    }
    
    private void testSetOfOne(Set<?> set, Object item) {
        Assert.assertNotNull(set);
        Assert.assertNotNull(item);
        
        Assert.assertTrue(set.size() == 1);
        
        Object setItem = null;
        for (Object candidate : set) {
            setItem = candidate;
        }
        
        Assert.assertSame(item, setItem);
        
    }

}
