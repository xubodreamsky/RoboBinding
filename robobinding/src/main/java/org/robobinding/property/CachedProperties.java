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
package org.robobinding.property;

import java.util.Map;

import org.robobinding.internal.com_google_common.collect.Maps;




/**
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
public class CachedProperties implements Properties
{
	private PropertyCreator propertyCreator;
	private Map<String, Property<?>> propertyCache;
	public CachedProperties(Object bean)
	{
		propertyCreator = new PropertyCreator(bean);
		propertyCache = Maps.newHashMap();
	}

	@Override
	public Class<?> getPropertyType(String propertyName)
	{
		Property<?> property = getReadOnlyProperty(propertyName);
		return property.getPropertyType();
	}

	@Override
	public <T> Property<T> getReadWriteProperty(String propertyName)
	{
		return getProperty(propertyName, true);
	}

	@Override
	public <T> Property<T> getReadOnlyProperty(String propertyName)
	{
		return getProperty(propertyName, false);
	}

	private <T> Property<T> getProperty(String propertyName, boolean isReadWriteProperty)
	{
		@SuppressWarnings("unchecked")
		Property<T> property = (Property<T>)propertyCache.get(propertyName);
		if(property == null)
		{
			property = propertyCreator.createProperty(propertyName);
			propertyCache.put(propertyName, property);
		}
		property.checkReadWriteProperty(isReadWriteProperty);
		return property;
	}

	@Override
	public <T> DataSetProperty<T> getReadOnlyDataSetProperty(String propertyName)
	{
		@SuppressWarnings("unchecked")
		DataSetProperty<T> property = (DataSetProperty<T>)propertyCache.get(propertyName);
		if(property == null)
		{
			property = propertyCreator.createDataSetProperty(propertyName);
			propertyCache.put(propertyName, property);
		}
		property.checkReadWriteProperty(false);
		return property;
	}

}
