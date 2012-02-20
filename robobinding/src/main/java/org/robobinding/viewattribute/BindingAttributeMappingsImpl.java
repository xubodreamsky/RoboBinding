/**
 * Copyright 2011 Cheng Wei, Robert Taylor
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
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.robobinding.viewattribute;

import java.util.Collection;
import java.util.Map;

import org.robobinding.viewattribute.view.ViewListeners;
import org.robobinding.viewattribute.view.ViewListenersAware;

import android.view.View;

import com.google.common.collect.Maps;

/**
 * 
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Robert Taylor
 * @author Cheng Wei
 */
public class BindingAttributeMappingsImpl<T extends View> implements BindingAttributeMappings<T>
{
	private T view;
	private boolean preInitializeViews;
	private ViewListenersProvider viewListenersProvider;
	
	private Map<String, Class<? extends PropertyViewAttribute<? extends View>>> propertyViewAttributeMappings;
	private Map<String, Class<? extends AbstractCommandViewAttribute<? extends View>>> commandViewAttributeMappings;
	private Map<GroupedAttributeDetailsImpl, Class<? extends AbstractGroupedViewAttribute<? extends View>>> groupedViewAttributeMappings;
	
	public BindingAttributeMappingsImpl(T view, boolean preInitializeViews, ViewListenersProvider viewListenersProvider)
	{
		this.view = view;
		this.preInitializeViews = preInitializeViews;
		this.viewListenersProvider = viewListenersProvider;
		
		propertyViewAttributeMappings = Maps.newHashMap();
		commandViewAttributeMappings = Maps.newHashMap();
		groupedViewAttributeMappings = Maps.newHashMap();
	}

	@Override
	public void mapPropertyAttribute(Class<? extends PropertyViewAttribute<T>> propertyViewAttributeClass, String attributeName)
	{
		addPropertyViewAttributeMapping(propertyViewAttributeClass, attributeName);
	}

	@Override
	public void mapCommandAttribute(Class<? extends AbstractCommandViewAttribute<T>> commandViewAttributeClass, String attributeName)
	{
		addCommandViewAttributeMapping(commandViewAttributeClass, attributeName);
	}

	@Override
	public void mapGroupedAttribute(Class<? extends AbstractGroupedViewAttribute<T>> groupedViewAttributeClass,	String... attributeNames)
	{
		addGroupedViewAttributeMapping(groupedViewAttributeClass, attributeNames);
	}

	protected void addPropertyViewAttributeMapping(Class<? extends PropertyViewAttribute<?>> propertyViewAttributeClass, String attributeName)
	{
		propertyViewAttributeMappings.put(attributeName, propertyViewAttributeClass);
	}
	
	protected void addCommandViewAttributeMapping(Class<? extends AbstractCommandViewAttribute<?>> commandViewAttributeClass, String attributeName)
	{
		commandViewAttributeMappings.put(attributeName, commandViewAttributeClass);
	}
	
	protected void addGroupedViewAttributeMapping(Class<? extends AbstractGroupedViewAttribute<?>> groupedViewAttributeClass,	String... attributeNames)
	{
		GroupedAttributeDetailsImpl groupedPropertyAttribute = new GroupedAttributeDetailsImpl(attributeNames);
		groupedViewAttributeMappings.put(groupedPropertyAttribute, groupedViewAttributeClass);
	}
	
	public Collection<String> getPropertyAttributes()
	{
		return propertyViewAttributeMappings.keySet();
	}

	public PropertyViewAttribute<View> createPropertyViewAttribute(String propertyAttribute, String attributeValue)
	{
		Class<? extends PropertyViewAttribute<? extends View>> propertyViewAttributeClass = propertyViewAttributeMappings.get(propertyAttribute);
		@SuppressWarnings("unchecked")
		PropertyViewAttribute<View> propertyViewAttribute = (PropertyViewAttribute<View>) newViewAttribute(propertyViewAttributeClass);
		View view = getViewForAttribute(propertyAttribute);
		propertyViewAttribute.setView(view);
		propertyViewAttribute.setAttributeValue(attributeValue);
		propertyViewAttribute.setPreInitializeView(preInitializeViews);
		setViewListenersIfRequired(propertyViewAttribute, view);
		return propertyViewAttribute;
	}

	public Collection<String> getCommandAttributes()
	{
		return commandViewAttributeMappings.keySet();
	}

	public AbstractCommandViewAttribute<View> createCommandViewAttribute(String commandAttribute, String attributeValue)
	{
		Class<? extends AbstractCommandViewAttribute<? extends View>> commandViewAttributeClass = commandViewAttributeMappings.get(commandAttribute);
		@SuppressWarnings("unchecked")
		AbstractCommandViewAttribute<View> commandViewAttribute = (AbstractCommandViewAttribute<View>) newViewAttribute(commandViewAttributeClass);
		View view = getViewForAttribute(commandAttribute);
		commandViewAttribute.setView(view);
		commandViewAttribute.setCommandName(attributeValue);
		setViewListenersIfRequired(commandViewAttribute, view);
		return commandViewAttribute;
	}

	protected View getViewForAttribute(String attributeName)
	{
		return view;
	}
	
	public Collection<GroupedAttributeDetailsImpl> getGroupedPropertyAttributes()
	{
		return groupedViewAttributeMappings.keySet();
	}

	public AbstractGroupedViewAttribute<View> createGroupedViewAttribute(GroupedAttributeDetailsImpl groupedAttributeDetails)
	{
		Class<? extends AbstractGroupedViewAttribute<? extends View>> groupedViewAttributeClass = groupedViewAttributeMappings.get(groupedAttributeDetails);
		@SuppressWarnings("unchecked")
		AbstractGroupedViewAttribute<View> groupedViewAttribute = (AbstractGroupedViewAttribute<View>)newViewAttribute(groupedViewAttributeClass);
		View view = getViewForGroupedAttribute(groupedAttributeDetails);
		groupedViewAttribute.setView(view);
		groupedViewAttribute.setPreInitializeViews(preInitializeViews);
		groupedViewAttribute.setGroupedAttributeDetails(groupedAttributeDetails);
		setViewListenersIfRequired(groupedViewAttribute, view);
		groupedViewAttribute.postInitialization();
		return groupedViewAttribute;
	}
	
	protected View getViewForGroupedAttribute(GroupedAttributeDetailsImpl groupedAttributeDetails)
	{
		return view;
	}
	
	private void setViewListenersIfRequired(ViewAttribute viewAttribute, View view)
	{
		if(viewAttribute instanceof ViewListenersAware)
		{
			ViewListeners viewListeners = viewListenersProvider.forView(view);
			@SuppressWarnings("unchecked")
			ViewListenersAware<ViewListeners> viewListenersAware = (ViewListenersAware<ViewListeners>)viewAttribute;
			viewListenersAware.setViewListeners(viewListeners);
		}
	}

	private ViewAttribute newViewAttribute(Class<? extends ViewAttribute> viewAttributeClass)
	{
		try
		{
			return viewAttributeClass.newInstance();
		} catch (InstantiationException e)
		{
			throw new RuntimeException("Attribute class " + viewAttributeClass.getName() + " does not have an empty default constructor");
		} catch (IllegalAccessException e)
		{
			throw new RuntimeException("Attribute class " + viewAttributeClass.getName() + " is not public");
		}
	}
	
}