function topicChangedConsumer(){
       var topicName = document.getElementById("topicsSelect").value;
       if( topicName == "select one")
            document.getElementById("eventPresenter").style.visibility = "hidden";
       else{
            document.getElementById("eventPlace").innerHTML = "";
            fetchTopicEvents(topicName);
       }
}

function fetchEventCallBack(data){
    var dataPlace = document.getElementById("eventPresenter");
    if(data == "EOQ")
        alert("Reach End of Queue");
    else if(data == "NCK")
        alert("Bad request");
    else{
        document.getElementById("eventPresenter").style.visibility = "visible";
        document.getElementById("eventPlace").innerHTML = "event:" + data.data;
    }
}

function fetchTopicEvents(topicName){
    $.get(topicName,
          function(data, status){
                fetchEventCallBack(data);
          }
    );
}

function nextEvent(){
    var topicName = document.getElementById("topicsSelect").value;
    fetchTopicEvents(topicName);
}