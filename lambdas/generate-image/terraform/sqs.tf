# SQS Queue
resource "aws_sqs_queue" "demo_queue" {
  name = var.queue_name
}
