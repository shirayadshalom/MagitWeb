var refreshRate = 2000;
var USER_LIST_URL = buildUrlWithContextPath("userslist");
var REPOS_TABLE_URL=buildUrlWithContextPath("/magit/repos");
var USERNAME_URL=buildUrlWithContextPath("/userName");
var NOTIFICATION_URL=buildUrlWithContextPath("/notifications");
var CHAT_LIST_URL = buildUrlWithContextPath("/chat");
var chatVersion = 0;
var userNameDisplayRepo;
var user;

// ["moshe","nachum","nachche"...]
function refreshUsersList(users) {
    $("#userslist").empty();
    $.each(users || [], function(index, username) {
        if (user===username){
            $('<li><a href="#" onclick=myFunction($(this).text());><b>' + username + '</b></a></li>').appendTo($("#userslist"));
        }
        else{
        //console.log("Adding user #" + index + ": " + username);
        $('<li><a href="#" onclick=myFunction($(this).text());>' + username + '</a></li>').appendTo($("#userslist"));}
        //console.log(this);
    });


}

function refreshReposTable(repos) {
    /* [ { "activeBranch":"Hi", "name":"bbb","lastCommitMessage":"blblblb","lastCommitDate": "11/2/29", "numOfBranches":"3"},... ] */
    $("tbody").children().remove();
    $.each(repos || [], function(index, data) {
        console.log(index + ": " + data);
        var tr="<tr  onclick=myRepo($(this).find(\"td[id='name']\").text());>";
        var td1="<td id=\"name\">"+data["name"]+"</td>";
        var td2="<td>"+data["activeBranch"]+"</td>";
        var td3="<td>"+data["numOfBranches"]+"</td>";
        var td4="<td>"+data["lastCommitDate"]+"</td>";
        var td5="<td>"+data["lastCommitMessage"]+"</td></tr>";
        $("#RepoTable").find('tbody').append(tr+td1+td2+td3+td4+td5);
    });
}
function refreshNotifications(lst) {
        /*
          {"forks":["rep 1 was forked by itamar2",...]
          "prs":[prdata,prdata...]}
          */
        $("#notice_fork").empty();
        $("#notice_PR").empty();
        $.each(lst || [], function(index, data) {
            var notice_fork = "This is a fork notice:" + '<br>';
            var notice_pr = "This is a PR notice:" + '<br>';
            if (index=== "forks"){
                console.log(data);
                notice_fork=notice_fork.concat(data);
                $("#notice_fork").append(notice_fork);
            }
            if (index=== "PRs"){
                console.log(data);
                notice_pr=notice_pr.concat(data);
                $("#notice_PR").append(notice_pr);
            }
        });
}

function refreshAnotherUserReposTable() {
    window.open("anotherUserRepos.html?data="+userNameDisplayRepo, "toolbar=yes,scrollbars=yes,resizable=yes,top=500,left=500,width=600,height=600");
}

function ajaxNotifications() {
    $.ajax({
        url: NOTIFICATION_URL,
        success: function(data) {
            /*
          {"forks":["rep 1 was forked by itamar2",...]
          "prs":[prdata,prdata...]}
          */
            console.log(data);
            refreshNotifications(data);
        }
    });
}


function ajaxUsersList() {
    $.ajax({
        url: USER_LIST_URL,
        success: function(users) {
            refreshUsersList(users);
        }
    });
}

function ajaxReposTable() {
    $.ajax({
        url: REPOS_TABLE_URL,
        type: "get",
       data: {username: user},
        success: function(data) {
            /* [ { "activeBranch":"Hi", "name":"bbb","lastCommitMessage":"blblblb","lastCommitDate": "11/2/29", "numOfBranches":"3"},... ] */
            console.log(data);
                refreshReposTable(data);
        }
    });
}

function ajaxAnotherUserReposTable() {
    refreshAnotherUserReposTable();
}


$(function() {
    $("#uploadForm").submit(function() {
        var formData = new FormData();
        var file1 = this[0].files[0];
        formData.append("fake-key-1", file1);

        $.ajax({
            method:'POST',
            data: formData,
            url: this.action,
            processData: false,
            contentType: false,
            timeout: 4000,
            error: function(e) {
                console.error(e);
                alert("Failed to get result from server " + e);
            },
            success: function(r) {
                console.log(r);
                if (r.toString().trim()=="repo created"){
                    alert("success");
                    ajaxReposTable();
                }
                else
                alert(r.toString()+" Try again!");
            }
        });
        return false;
    })
})


$(function() { // onload...do
    $("#chatform").submit(function() {
        $.ajax({
            data: $(this).serialize(),
            url: this.action,
            timeout: 2000,
            error: function() {
                console.error("Failed to submit");
            },
            success: function(r) {
            }
        });

        $("#userstring").val("");
        return false;
    });
});



$(function() {
    setInterval(ajaxUsersList, refreshRate);
    triggerAjaxChatContent();
});

function triggerAjaxChatContent() {
    setTimeout(ajaxChatContent, refreshRate);
}

function appendToChatArea(entries) {

    $.each(entries || [], appendChatEntry);
    var scroller = $("#chatarea");
    var height = scroller[0].scrollHeight - $(scroller).height();
    $(scroller).stop().animate({ scrollTop: height }, "slow");
}

function appendChatEntry(index, entry){
    var entryElement = createChatEntry(entry);
    $("#chatarea").append(entryElement).append("<br>");
}

function createChatEntry (entry){
    entry.chatString = entry.chatString.replace (":)", "<img class='smiley-image' src='../../common/images/smiley.png'/>");
    return $("<span class=\"success\">").append(entry.username + "> " + entry.chatString);
}

function ajaxChatContent() {
    $.ajax({
        url: CHAT_LIST_URL,
        data: "chatversion=" + chatVersion,
        dataType: 'json',
        timeOut: refreshRate,
        success: function(data) {
            console.log("Server chat version: " + data.version + ", Current chat version: " + chatVersion);
            if (data.version !== chatVersion) {
                chatVersion = data.version;
                appendToChatArea(data.entries);
            }
            triggerAjaxChatContent();
        },
        error: function(error) {
            triggerAjaxChatContent();
        }
    });
}

$(function() {
    setInterval(ajaxNotifications, refreshRate);
});


function myRepo(name) {
    window.open("../repository/repository.html?data="+name, "_self","toolbar=yes,scrollbars=yes,resizable=yes,top=500,left=500,width=600,height=600");
}


function myFunction(data) {
    userNameDisplayRepo=data;
    console.log("userNameDisplayRepo: " + userNameDisplayRepo);
    console.log("user: "+ user);
    if (userNameDisplayRepo!==user) {ajaxAnotherUserReposTable(data);}
}


$(function() {
    $.ajax({
        url: USERNAME_URL,
        success: function(data) {
            user=data.trim();
            userNameDisplayRepo=data.trim();
            console.log("user" + user + ": " +"userNameDisplayRepo" +userNameDisplayRepo);
            ajaxReposTable();
        }
    });
});

function openForm() {
    document.getElementById("myChat").style.display = "block";
}

function closeForm() {
    document.getElementById("myChat").style.display = "none";
}







