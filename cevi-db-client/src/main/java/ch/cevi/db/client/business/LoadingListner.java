package ch.cevi.db.client.business;

public interface LoadingListner<K> {

	public void displayCurrentAction(String currentAction);

	public void loadingFinished(K result, Exception e);

}
