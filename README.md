# LittleToys
For the implementation of some small games or simple tools

## [Sudoku](https://github.com/LeZhouYi/LittleToys/blob/main/instance/suduku.jar)
#### 初衷:
- 游玩数独有时力不从心，想要工具来尝试模拟/辅助人推演时过程并逐步完善数独
- 因此没有完全实现数独的完全解谜，需要手工输入格子和调用推测功能。并且某些数独用我掌握的推测方法不能推测出唯一值，但够用了

#### 操作:
- 左击格子:
  - 弹出当前可填数，若格子未有值且没有可填数，说明数独已填错
- 中击格子:
  - 若当前格子有输入值，则锁定；
  - 若当前格子为锁定状态，则解锁；
- 右击格子：
  - 清空当前格子的输入
- TAB: 弹出功能菜单
  - 预填:
    - 寻找只有一个可填数的格子并提示
  - 推测:
    - 根据推测方法清除格子中错误的可填数
  - 备份:
    - 备份当前数独并在此菜单出现该存档加载入口
    - 关闭程序不保留存档
  - 清空:
    - 清空当前数独和存档

#### 推测方法:
1. 若在xy格填入v值，若xy格对应x行，y列和所在的宫，那些未填格子中的可选值排除v值
2. 若某宫内的v值的可填格子在同x行/y列，则将宫外x行/y列的未填格子中的可选值排除v值
3. 若某一行/列的v值只有一个可填，清除该格子v值外的可选值
4. 若某宫内的未填格子只有两个可选值，且这些格子形成vw-vw型，则消除宫内其它未填格子的vw两个可选值（vw-vu-uw型未实现）
