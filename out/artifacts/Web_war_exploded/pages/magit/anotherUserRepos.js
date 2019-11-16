var REPOS_TABLE_URL=buildUrlWithContextPath("/magit/repos");
var CLONE_URL=buildUrlWithContextPath("magit/clone");
var user;
var remoteRepo_name;


function refreshTable(repos){
$.each(repos || [], function(index, data) {
    console.log(index + ": " + data);
    var name=data["name"];
    var tr="<tr class=\"clickOnMe\" onclick=myFunction($(this).find(\"td[id='name']\").text());>";
    var td1="<td id=\"name\">"+data["name"]+"</td>";
    var td2="<td>"+data["activeBranch"]+"</td>";
    var td3="<td>"+data["numOfBranches"]+"</td>";
    var td4="<td>"+data["lastCommitDate"]+"</td>";
    var td5="<td>"+data["lastCommitMessage"]+"</td></tr>";
    $("#anotherRepoTable").find('tbody').append(tr+td1+td2+td3+td4+td5);
});
}

function ajaxReposTable() {
    $.ajax({
        url: REPOS_TABLE_URL,
        type: "get",
        data: {username: user},
        success: function(data) {
            console.log(data);
            /* [ { "activeBranch":"Hi", "name":"bbb","lastCommitMessage":"blblblb","lastCommitDate": "11/2/29", "numOfBranches":"3"},... ] */
            refreshTable(data);
        }
    });
}



function ajaxClone(){
    $.ajax({
        url: CLONE_URL,
        type: "get",
        data: {RR: remoteRepo_name,
            name:user},
        success: function(data) {
            alert(data)
            window.open("../magit/magit.html?user="+user, "_self","toolbar=yes,scrollbars=yes,resizable=yes,top=500,left=500,width=600,height=600");
        }
    });
}


$(function() {
    user=decodeURI(location.search.split('data=')[1]);
    //repo_name=decodeURI(location.search.split("&")[1]);
    //sha1_=decodeURI(location.search.split('sha1=')[1]);
    console.log(user);
});


$(function() {
    ajaxReposTable();
});

$(document).ready(function(){
    $(".clickOnMe").tooltip({title: "Click on me to to fork", placement: "top" });
});


function myFunction(name) {
    remoteRepo_name=name.trim();
    $("#RRname").text("Remote repo: "+remoteRepo_name);
    console.log(remoteRepo_name);
}

$(document).ready(function() {
    $( "#fork" ).click(function(){
        if (remoteRepo_name==undefined){
            alert("Select a specific REPO after that click FORK")
        }
        else{}
        ajaxClone();
    });
});

/*
function myLR(name) {
    localRepo_name=name.trim();
    console.log(localRepo_name);
    var newReponame = prompt("Enter repository name");
    if (newReponame == null || newReponame == "") {
        alert("User cancelled the prompt.");}
    else{
        ajaxClone(newReponame);
    }
}

 */


