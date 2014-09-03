package de.unipotsdam.nexplorer.client.indoor.view.messaging;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

import de.unipotsdam.nexplorer.client.indoor.levels.Route;
import de.unipotsdam.nexplorer.client.indoor.levels.RouteListener;

public class LevelTwoRouteSelection extends RoutingLevel implements RouteListener {

	private static LevelTwoRouteSelectionUiBinder uiBinder = GWT.create(LevelTwoRouteSelectionUiBinder.class);

	interface LevelTwoRouteSelectionUiBinder extends UiBinder<Element, LevelTwoRouteSelection> {
	}

	@UiField
	DivElement routes;

	private final HashMap<Route, RouteBinder> routesModel;
	private final Collection<RouteClickListener> clickObservers;

	public LevelTwoRouteSelection() {
		setElement(uiBinder.createAndBindUi(this));
		this.routesModel = new HashMap<Route, RouteBinder>();
		this.clickObservers = new LinkedList<RouteClickListener>();
	}

	@Override
	public void updateRoutes(List<Route> routes) {
		removeObsolete(routes);
		addNew(routes);
	}

	private void removeObsolete(List<Route> routes) {
		Iterator<Entry<Route, RouteBinder>> viewedRoutes = routesModel.entrySet().iterator();
		while (viewedRoutes.hasNext()) {
			Entry<Route, RouteBinder> viewedRoute = viewedRoutes.next();
			if (!routes.contains(viewedRoute.getKey())) {
				viewedRoute.getValue().getElement().removeFromParent();
				viewedRoutes.remove();
			}
		}
	}

	private void addNew(List<Route> routes) {
		for (Route route : routes) {
			if (!routesModel.containsKey(route)) {
				RouteBinder view = new RouteBinder(route);
				view.setClickHandler(new Notify(clickObservers), this);
				
				Route nextChildKey = getNextChild(routesModel.keySet(), route);
				if(nextChildKey != null){
					RouteBinder nextChild = routesModel.get(nextChildKey);
					this.routes.insertBefore(view.getElement(), nextChild.getElement());
				} else {
					this.routes.appendChild(view.getElement());
				}
				routesModel.put(route, view);
			}
		}
	}
	
	private Route getNextChild(Set<Route> routes, Route route){
		Route response = null;
		Iterator<Route> iter = routes.iterator();
	    while (iter.hasNext()) {
	      Route nextRoute = (Route)iter.next();
	      //sortieren der Liste, ziemlich simpel gehalt, aber fuer kleine Listen ausreichend
	      if(Integer.parseInt(nextRoute.getSource()) > Integer.parseInt(route.getSource())
	    		  || (Integer.parseInt(nextRoute.getSource()) == Integer.parseInt(route.getSource())
	    		  && Integer.parseInt(nextRoute.getDestination()) > Integer.parseInt(route.getDestination()))){
	    	  
	    		  if(response == null){
	    			  response = nextRoute;
	    		  } else if(Integer.parseInt(nextRoute.getSource()) < Integer.parseInt(response.getSource())){
	    			  response = nextRoute;
	    		  } else if(Integer.parseInt(nextRoute.getSource()) == Integer.parseInt(response.getSource()) && Integer.parseInt(nextRoute.getDestination()) < Integer.parseInt(response.getDestination())){
	    			  response = nextRoute;
	    		  }
	    	  }
	    	  
	      } 
	
		return response;
	}

	public void addClickHandler(final RouteClickListener listener) {
		this.clickObservers.add(listener);
	}
}
