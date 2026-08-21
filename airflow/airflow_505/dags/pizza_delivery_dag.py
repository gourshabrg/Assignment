import logging
import random
from datetime import datetime, timedelta
from airflow import DAG
from airflow.operators.python import PythonOperator, BranchPythonOperator
from airflow.operators.bash import BashOperator
from airflow.utils.trigger_rule import TriggerRule

logger = logging.getLogger("airflow.task")

DEFAULT_ARGS = {
    'owner': 'doughflow_eng',
    'depends_on_past': False,
    'start_date': datetime(2026, 1, 1),
    'email_on_failure': False,
    'retries': 1,
    'retry_delay': timedelta(minutes=2),
}

def _accept_order(ti):
   
    order_id = f"DFP-{random.randint(10000, 99999)}"
    toppings = random.choice([["pepperoni", "mushroom"], ["pineapple", "ham"], ["basil", "mozzarella"]])
    
    logger.info(f"[ORDER RECEIPT] Received new request. Generated {order_id}.")
    logger.debug(f"Target customization blueprint items: {toppings}")
    
    # Push to downstream tasks via XCom
    ti.xcom_push(key='order_id', value=order_id)
    ti.xcom_push(key='topping_list', value=toppings)

def _validate_inventory(ti):
    
    order_id = ti.xcom_pull(task_ids='accept_order', key='order_id')
    
   
    if random.random() < 0.15:
        logger.critical(f"[INVENTORY CRISIS] Missing essential dough or sauce batches for {order_id}!")
        return 'trigger_refund'
    
    logger.info(f"[INVENTORY VERIFIED] All components available for {order_id}. Routing to standard prep lines.")
    return 'toss_dough'

def _apply_toppings(ti):
    """Pulls topping array from upstream task metadata."""
    order_id = ti.xcom_pull(task_ids='accept_order', key='order_id')
    toppings = ti.xcom_pull(task_ids='accept_order', key='topping_list')
    
    logger.info(f"[ASSEMBLY] Spreading signature organic sauce onto order {order_id}.")
    logger.info(f"[ASSEMBLY] Evenly spreading requested toppings: {', '.join(toppings)}.")

with DAG(
    dag_id='pizza_delivery_pipeline',
    default_args=DEFAULT_ARGS,
    description='Automated assembly, cooking, and dispatch line for DoughFlow Pizza Co.',
    schedule_interval='55 17 * * *',
    catchup=False,
    tags=['production', 'kitchen_automation']
) as dag:

    accept_order = PythonOperator(
        task_id='accept_order',
        python_callable=_accept_order
    )

    validate_inventory = BranchPythonOperator(
        task_id='validate_inventory',
        python_callable=_validate_inventory
    )

    toss_dough = BashOperator(
        task_id='toss_dough',
        bash_command='echo "Executing automated spin sequence at 1200 RPM..." && sleep 3',
    )

    apply_toppings = PythonOperator(
        task_id='apply_toppings',
        python_callable=_apply_toppings
    )

    trigger_refund = BashOperator(
        task_id='trigger_refund',
        bash_command='echo "CRITICAL SYSTEM NOTICE: Processing immediate payment reversals via Stripe API..."',
    )


    bake_pizza = BashOperator(
        task_id='bake_pizza',
        bash_command='echo "Tunnel convection oven configured. Passing pizza under thermal grid..." && sleep 5',
        trigger_rule=TriggerRule.NONE_FAILED_MIN_ONE_SUCCESS
    )

    dispatch_order = BashOperator(
        task_id='dispatch_order',
        bash_command='echo "Handoff complete. Launching autonomous drone delivery routing matrix."',
    )

    accept_order >> validate_inventory
    validate_inventory >> toss_dough >> apply_toppings >> bake_pizza
    validate_inventory >> trigger_refund >> bake_pizza
    bake_pizza >> dispatch_order
