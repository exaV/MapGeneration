package ch.fhnw.util;

/**
 * Implementing this interface accelerates ClassUtilities.identityHashCode(). Use ClassUtilities.createObjectId() to get a new id.
 * 
 * @author sschubiger
 */
public interface IObjectID {
	long getObjectID();
}
