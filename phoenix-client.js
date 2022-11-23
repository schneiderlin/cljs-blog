// import { Socket } from "phoenix"
const { Socket } = require('phoenix')

let socket = new Socket("ws://localhost:4000/socket", {params: {userToken: "123"}})
socket.connect()