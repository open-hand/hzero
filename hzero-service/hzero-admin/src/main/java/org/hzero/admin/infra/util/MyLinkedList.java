package org.hzero.admin.infra.util;

/**
 * 自定义LinkedList
 *
 * @author modify by bo.he02@hand-china.com on 2020/05/11
 * @author superlee
 */
public class MyLinkedList<T> {
    /**
     * 头节点
     */
    Node head = null;

    /**
     * 向链表中插入数据
     *
     * @param data 新增的节点的数据
     */
    public void addNode(T data) {
        // 实例化一个节点
        Node newNode = new Node(data);
        if (this.head == null) {
            this.head = newNode;
            return;
        }
        Node tmp = this.head;
        while (tmp.next != null) {
            tmp = tmp.next;
        }
        tmp.next = newNode;
    }

    /**
     * 判断链表是否有环，单向链表有环时，尾节点相同
     *
     * @return 判断结果    true 有环,false 无环
     */
    public boolean isLoop() {
        Node fast = this.head;
        Node slow = this.head;
        if (fast == null) {
            return false;
        }
        while (slow.next != null) {
            fast = fast.next;
            if (fast.data.equals(slow.data)) {
                return true;
            }
            if (fast.next == null) {
                slow = slow.next;
                fast = slow;
            }
        }
        return false;
    }

    /**
     * 深拷贝
     *
     * @return 拷贝结果对象
     */
    public MyLinkedList<T> deepCopy() {
        MyLinkedList<T> myLinkedList = new MyLinkedList<>();
        Node tempHead = this.head;
        while (tempHead != null) {
            myLinkedList.addNode(tempHead.data);
            tempHead = tempHead.next;
        }
        return myLinkedList;
    }

    /**
     * 节点对象
     */
    class Node {
        // 节点的引用，指向下一个节点
        Node next = null;
        // 节点的对象，即内容
        T data;

        public Node(T data) {
            this.data = data;
        }
    }
}
