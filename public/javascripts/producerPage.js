function topicChangedInProducer(){
       var x = document.getElementById("topicsSelect").value;
       if (x == "createNew")
            changeForm("Create topic");
       else if( x == "selectOne")
            document.getElementById("formsPlace").style.visibility = "hidden";
       else
            changeForm("Add event");
}

function changeForm(applyChangeButtonName){
    document.getElementById("formsPlace").style.visibility = "visible";
    document.getElementById("applyChangeButton").value = applyChangeButtonName;
}

function getChanges(){
    var textValue = document.getElementById("eventNameText").value;
    var buttonName = document.getElementById("applyChangeButton").value;
    var isNewTopic = true;
    var topicName = "";

    if(buttonName == "Add event"){
        isNewTopic = false;
        topicName = document.getElementById("topicsSelect").value;
    }

    postChanges(isNewTopic, textValue, topicName)
}

function updateSelectTag(topicName) {
    var selectTag = document.getElementById("topicsSelect");
    var optionTag = document.createElement("option");
    optionTag.text = topicName;
    optionTag.value = topicName;
    var index = selectTag.length;
    selectTag.add(optionTag,selectTag[index - 1]);
}

function postCreateNewTopicCallBack(data,textValue){
    if(data == "ACK"){
        document.getElementById("eventNameText").value = "" ;
        updateSelectTag(textValue);
    }
    else
        alert("Topic Already Exist");
}

function postCreateNewEventCallBack(data){
    if(data == "ACK")
        document.getElementById("eventNameText").value = "" ;
    else
        alert("Unable to add new event");
}

function postChanges(isNewTopic, textValue, topicName){
    console.log(topicName)
    if(isNewTopic)
        $.post("createNewTopic",{data:textValue},function(data,status){postCreateNewTopicCallBack(data,textValue)});
    else
        $.post(topicName,{data:textValue},function(data,status){postCreateNewEventCallBack(data)});
}

