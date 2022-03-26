import { AXIOS } from "../common/AxiosScript";

export default {
    name: 'completedOrderList',
    data () {
      return {
        purchase: {
            id:'',
            items:'',
            state:'',
            date_of_purchase:'',
            time_of_purchase:'',
            delivery:'',
        },

        purchases: [],
        errorCompletedpurchases: '',
        response: []
      }
    },

    created: function () {
        AXIOS.get('/purchase/allCompleted')
        .then(response => {
            this.purchases = response.data;
        })
        .catch(e => {
        this.errorCompletedpurchases = e;
        })
      },
    
 }