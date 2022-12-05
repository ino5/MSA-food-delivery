
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);


import OrderManager from "./components/listers/OrderCards"
import OrderDetail from "./components/listers/OrderDetail"

import TestManager from "./components/listers/TestCards"
import TestDetail from "./components/listers/TestDetail"

import Test3Manager from "./components/listers/Test3Cards"
import Test3Detail from "./components/listers/Test3Detail"


import MypageView from "./components/MypageView"
import MypageViewDetail from "./components/MypageViewDetail"

export default new Router({
    // mode: 'history',
    base: process.env.BASE_URL,
    routes: [
            {
                path: '/orders',
                name: 'OrderManager',
                component: OrderManager
            },
            {
                path: '/orders/:id',
                name: 'OrderDetail',
                component: OrderDetail
            },

            {
                path: '/tests',
                name: 'TestManager',
                component: TestManager
            },
            {
                path: '/tests/:id',
                name: 'TestDetail',
                component: TestDetail
            },

            {
                path: '/test3s',
                name: 'Test3Manager',
                component: Test3Manager
            },
            {
                path: '/test3s/:id',
                name: 'Test3Detail',
                component: Test3Detail
            },


            {
                path: '/mypages',
                name: 'MypageView',
                component: MypageView
            },
            {
                path: '/mypages/:id',
                name: 'MypageViewDetail',
                component: MypageViewDetail
            },


    ]
})
