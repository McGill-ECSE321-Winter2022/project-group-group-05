<!--Visibility: employee, owner-->
<!--View all completed order-->
<template>
    <div id="completedOrderList">

        <div class="back-to-previous-style">
            <a href="javascript:history.back()">Previous Page</a>
        </div>

        <h1 class="header_style">Completed Orders</h1><br>

        <table class="tablestyle" align="center">
            <tr class="trstyle">
                <th class="head_thstyle"> id </th>
                <th class="head_thstyle"> items </th>
                <th class="head_thstyle"> state </th>
                <th class="head_thstyle"> date of purchase </th>
                <th class="head_thstyle"> time of purchase</th>
                <th class="head_thstyle"> delivery </th>
            </tr>
            <tr class="trstyle" v-for="purchase in pruchases" :key="purchase.id">
                <td class="tdstyle">{{purchase.id}}</td>
                <td class="tdstyle">{{purchase.items}}</td>
                <td class="tdstyle">{{purchase.state}}</td>
                <td class="tdstyle">{{purchase.date_of_purchase}}</td>
                <td class="tdstyle">{{purchase.time_of_purchase}}</td>
                <td class="tdstyle">{{purchase.delivery}}</td>
            </tr>
        </table>

        <br>

    </div>
</template>


<script src="./ViewCompletedOrdersScript.js">
</script>

<style scoped>
/* Styling for Page Title */
.header_style {
  padding-top: 15px;
  padding-left: 100px;
  border-color: #91c788;
  border-style: solid;
  border-width: 0px 0px 6px 0px;
  text-align: center;
  margin-bottom: 30px;
}
.completedOrderList{
    padding-bottom: 10px;
    margin-top: 10px;
    margin-left: auto;
    margin-right: auto;
    margin-bottom: auto;
    max-width: 80%;
    }
.tablestyle {
  font-family: arial, sans-serif;
  border-collapse: collapse;
  width: 70%;
}
.head_thstyle{
  text-align: center;
  font-size: 19px;
  border: 1px solid #245522;
  text-align: center;
  padding: 8px;
}
.tdstyle, .thstyle {
  border: 1px solid #dddddd;
  text-align: center;
  padding: 8px;
}
.trstyle:nth-child(even) {
  background-color: #5c5c64;
}
.back-to-previous-style {
  padding: 20px 1px;
  font-size: 20px;
  position: absolute;
  top: 0;
  left: 0;
  width: 180px;
  text-align: center;
}
a {
  color: #2410da;
}
</style>