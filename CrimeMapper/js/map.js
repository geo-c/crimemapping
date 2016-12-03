var CrimeLatLon = [];
var CrimeHeat = [];

function coordinate(x, y) {
    this.x = parseFloat(x);
    this.y = y;
}

var jqxhr = $.getJSON( "sparql", function() {
  	console.log( "success" );
	}).done(function() {
		console.log("second success");
		var JSONtext = jqxhr.responseText;
		var CrimeLocation= JSON.parse(JSONtext);
		for (var key in CrimeLocation.results.bindings){
		CrimeLatLon.push(new coordinate(CrimeLocation.results.bindings[key].lat.value,CrimeLocation.results.bindings[key].lon.value));
		/*CrimeLon.push(CrimeLocation.results.bindings[key].lon.value);*/
		}
		/*  for (var i = 0; i < CrimeLatLon.length; i++) {
	   marker = new L.marker([CrimeLatLon[i].x,CrimeLatLon[i].y])
		.addTo(map);
	  }*/

		for (var i = 1; i < CrimeLatLon.length; i++) {
			CrimeHeat.push([CrimeLatLon[i].x, CrimeLatLon[i].y, 1])
		}
		console.log(CrimeHeat);

		var heat = L.heatLayer(CrimeHeat, {radius: 35})
			.addTo(map);

	}).fail(function() {
			console.log( "error" );
	}).always(function() {
});



/**
 *  Leaflet map
 */
var map = L.map('map', {
	zoomControl : true
}).setView([51.509865, -0.118092], 10);

L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
	maxZoom : 18,
	attribution : 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, ' +
	'<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, '
	/* +
	'Imagery <a href="http://mapbox.com">Mapbox</a>',
	/*id : 'mapbox.streets'*/
}).addTo(map);

// add boroughs layer to map
$.getJSON("data/london_boroughs.geojson",function(boroughData){
    L.geoJson( boroughData, {
        style: function(){
            return { color: "#1c1499", weight: 2, fillOpacity: .0 };
        }/*,
		 // todo: show names of the boroughs?
		 onEachFeature: function( feature, layer ){
		 layer.bindPopup( "<strong>" + feature.properties.name + "</strong><br/>" )
		 }*/
    }).addTo(map);
});
