/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.jdbc.Size;

import org.dom4j.Node;

/**
 * @author Gavin King
 */
public class MetaType extends AbstractType {
	public static final String[] REGISTRATION_KEYS = new String[0];

	private final Type baseType;
	private final Map<Object,String> discriminatorValuesToEntityNameMap;
	private final Map<String,Object> entityNameToDiscriminatorValueMap;

	public MetaType(Map<Object,String> discriminatorValuesToEntityNameMap, Type baseType) {
		this.baseType = baseType;
		this.discriminatorValuesToEntityNameMap = discriminatorValuesToEntityNameMap;
		this.entityNameToDiscriminatorValueMap = new HashMap<String,Object>();
		for ( Map.Entry<Object,String> entry : discriminatorValuesToEntityNameMap.entrySet() ) {
			entityNameToDiscriminatorValueMap.put( entry.getValue(), entry.getKey() );
		}
	}

	public String[] getRegistrationKeys() {
		return REGISTRATION_KEYS;
	}

	public Map<Object, String> getDiscriminatorValuesToEntityNameMap() {
		return discriminatorValuesToEntityNameMap;
	}

	public int[] sqlTypes(Mapping mapping) throws MappingException {
		return baseType.sqlTypes(mapping);
	}

	@Override
	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
		return baseType.dictatedSizes( mapping );
	}

	@Override
	public Size[] defaultSizes(Mapping mapping) throws MappingException {
		return baseType.defaultSizes( mapping );
	}

	public int getColumnSpan(Mapping mapping) throws MappingException {
		return baseType.getColumnSpan(mapping);
	}

	public Class getReturnedClass() {
		return String.class;
	}

	public Object nullSafeGet(
		ResultSet rs,
		String[] names,
		SessionImplementor session,
		Object owner)
	throws HibernateException, SQLException {
		Object key = baseType.nullSafeGet(rs, names, session, owner);
		return key==null ? null : discriminatorValuesToEntityNameMap.get(key);
	}

	public Object nullSafeGet(
		ResultSet rs,
		String name,
		SessionImplementor session,
		Object owner)
	throws HibernateException, SQLException {
		Object key = baseType.nullSafeGet(rs, name, session, owner);
		return key==null ? null : discriminatorValuesToEntityNameMap.get(key);
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
	throws HibernateException, SQLException {
		baseType.nullSafeSet(st, value==null ? null : entityNameToDiscriminatorValueMap.get(value), index, session);
	}
	
	public void nullSafeSet(
			PreparedStatement st,
			Object value,
			int index,
			boolean[] settable, 
			SessionImplementor session) throws HibernateException, SQLException {
		if ( settable[0] ) {
			nullSafeSet(st, value, index, session);
		}
	}

	public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
		return toXMLString(value, factory);
	}
	
	public String toXMLString(Object value, SessionFactoryImplementor factory)
		throws HibernateException {
		return (String) value; //value is the entity name
	}

	public Object fromXMLString(String xml, Mapping factory)
		throws HibernateException {
		return xml; //xml is the entity name
	}

	public String getName() {
		return baseType.getName(); //TODO!
	}

	public Object deepCopy(Object value, SessionFactoryImplementor factory)
	throws HibernateException {
		return value;
	}

	public Object replace(
			Object original, 
			Object target,
			SessionImplementor session, 
			Object owner, 
			Map copyCache) {
		return original;
	}
	
	public boolean isMutable() {
		return false;
	}

	public boolean[] toColumnNullness(Object value, Mapping mapping) {
		throw new UnsupportedOperationException();
	}

	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session) throws HibernateException {
		return checkable[0] && isDirty(old, current, session);
	}
	
}
